package io.gloop.messenger;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.gloop.Gloop;
import io.gloop.GloopList;
import io.gloop.exceptions.GloopLoadException;
import io.gloop.messenger.dialogs.TaskInfoDialog;
import io.gloop.messenger.model.Chat;
import io.gloop.messenger.model.UserInfo;
import io.gloop.permissions.GloopUser;

public class ContactsFragment extends Fragment {

    private final static String SELECTED = "selected";
    private final static String NOT_SELECTED = "notSelected";

    private Context context;
    private GloopUser owner;
    private TaskAdapter taskAdapter;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static final int VIEW_CHATS = 0;
    public static final int VIEW_CLOSED_TASKS = 1;

    private int operation;
    private UserInfo userInfo;

    public static ContactsFragment newInstance(int operation, UserInfo userinfo, GloopUser owner) {
        ContactsFragment f = new ContactsFragment();
        Bundle args = new Bundle();
        args.putInt("operation", operation);
        args.putSerializable("userinfo", userinfo);
        args.putSerializable("owner", owner);
        f.setArguments(args);
        return f;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

// TODO requires permission on first start
//        ContactUtil.getPhoneNumbers(getContext());

//        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//        startActivityForResult(contactPickerIntent,1);
    }



//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        switch (requestCode){
//            case 1 :
//                if (resultCode == Activity.RESULT_OK) {
//                    Uri contactData = data.getData();
//
//                    Cursor cur =  getActivity().getContentResolver().query(contactData, null, null, null, null);
//                    if (cur.getCount() > 0) {// thats mean some resutl has been found
//                        if(cur.moveToNext()) {
//                            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
//                            String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                            Log.e("Names", name);
//
//                            if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
//                            {
//
//                                Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);
//                                while (phones.moveToNext()) {
//                                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                                    Log.e("Number", phoneNumber);
//                                }
//                                phones.close();
//                            }
//
//                        }
//                    }
//                    cur.close();
//                }
//                break;
//        }
//
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final RelativeLayout rv = (RelativeLayout) inflater.inflate(R.layout.fragment_list, container, false);
        setHasOptionsMenu(true);

        Bundle args = getArguments();
        operation = args.getInt("operation", 0);
        userInfo = (UserInfo) args.getSerializable("userinfo");
        this.owner = (GloopUser) args.getSerializable("owner");

        recyclerView = (RecyclerView) rv.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    ((MainActivity) getActivity()).setFABVisibility(View.INVISIBLE);
                } else {
                    ((MainActivity) getActivity()).setFABVisibility(View.VISIBLE);
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });

        this.context = getContext();

        mSwipeRefreshLayout = (SwipeRefreshLayout) rv.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5, R.color.color6);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setupRecyclerView();
            }
        });

        return rv;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                taskAdapter.filter(s);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                setupRecyclerView();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoadTasksTask extends AsyncTask<Void, Integer, GloopList<Chat>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mSwipeRefreshLayout == null) {
                if (getView() != null) {
                    mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh_layout);
                    mSwipeRefreshLayout.setColorSchemeResources(R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5, R.color.color6);
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            }
        }

        @Override
        protected GloopList<Chat> doInBackground(Void... urls) {
            GloopList<Chat> all = null;
//            if (operation == VIEW_CHATS)
                all = Gloop.all(Chat.class);
//            else
//                all = Gloop.all(Task.class).where().equalsTo("done", true).all();

            all.load();
            return all;
        }


        @Override
        protected void onPostExecute(GloopList<Chat> tasks) {
            super.onPostExecute(tasks);
            try {
                taskAdapter = new TaskAdapter(tasks);
                recyclerView.setAdapter(taskAdapter);
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void setupRecyclerView() {
        new LoadTasksTask().execute();
    }

    public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.BoardViewHolder> {

        private ArrayList<Chat> list;
        private final GloopList<Chat> originalList;

        TaskAdapter(final GloopList<Chat> tasks) {
            originalList = tasks;
            list = (ArrayList<Chat>) tasks.getLocalCopy();
            Collections.sort(list, Collections.reverseOrder(new Comparator<Chat>() {
                @Override
                public int compare(Chat left, Chat right) {
                    return Long.compare(left.getTimestamp(), right.getTimestamp());
                }
            }));
        }

        @Override
        public BoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new BoardViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final BoardViewHolder holder, int position) {
            final Chat chat = list.get(position);

            // TODO get other user then itself
//            holder.mContentView.setText(task.getUser2().getEmail());
            holder.mContentView.setText("test");
//            final int color = task.getColor();
//            holder.mImage.setBackgroundColor(color);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("chat", chat);
//                    intent.putExtra(TaskDetailFragment.ARG_BOARD, task);
//                    intent.putExtra(TaskDetailFragment.ARG_USER_INFO, userInfo);
                    context.startActivity(intent);
                }
            });

            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    int mWidth = getResources().getDisplayMetrics().widthPixels;
                    int mHeight = getResources().getDisplayMetrics().heightPixels;


                    new TaskInfoDialog(context, owner, chat, userInfo, mHeight / 2, mWidth / 2);
                    setupRecyclerView();
                    return true;
                }
            });

//            if (task.isDone()) {
//                holder.mTaskDone.setColorFilter(getContext().getResources().getColor(R.color.Yellow));
//                holder.mTaskDone.setTag(SELECTED);
//            } else {
//                holder.mTaskDone.setColorFilter(getContext().getResources().getColor(R.color.Gray));
//                holder.mTaskDone.setTag(NOT_SELECTED);
//            }
//            holder.mTaskDone.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (holder.mTaskDone.getTag().equals(NOT_SELECTED)) {
//                        holder.mTaskDone.setColorFilter(getContext().getResources().getColor(R.color.Yellow));
//                        holder.mTaskDone.setTag(SELECTED);
//                        task.setDone(true);
//                    } else {
//                        holder.mTaskDone.setColorFilter(getContext().getResources().getColor(R.color.Gray));
//                        holder.mTaskDone.setTag(NOT_SELECTED);
//                        task.setDone(false);
//                    }
//                    task.save();
//                    setupRecyclerView();
//                    if (userInfo != null)
//                        userInfo.saveInBackground();
//                }
//            });

        }

        @Override
        public int getItemCount() {
            try {
                if (list != null)
                    return list.size();
                else
                    return 0;
            } catch (GloopLoadException e) {
                e.printStackTrace();
                return 0;
            }
        }

        void filter(String s) {
            if (s.equals("")) {
                list = (ArrayList<Chat>) originalList.getLocalCopy();
            } else {
                String search = s.toLowerCase();
                for (Chat task : originalList) {
                    if (!task.getUser2().getUserName().toLowerCase().startsWith(search))   // TODO get other user then itself
                        list.remove(task);
                }
            }

            notifyDataSetChanged();
        }

        class BoardViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mContentView;
            final ImageView mImage;

            final List<CircleImageView> memberImages = new ArrayList<>();


            BoardViewHolder(View view) {
                super(view);
                mView = view.findViewById(R.id.card_view);
                mContentView = (TextView) view.findViewById(R.id.board_name);
                mImage = (ImageView) view.findViewById(R.id.avatar);

                memberImages.add((CircleImageView) view.findViewById(R.id.user_image1));
                memberImages.add((CircleImageView) view.findViewById(R.id.user_image2));
                memberImages.add((CircleImageView) view.findViewById(R.id.user_image3));
                memberImages.add((CircleImageView) view.findViewById(R.id.user_image4));
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}