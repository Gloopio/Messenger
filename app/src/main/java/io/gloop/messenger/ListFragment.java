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
import io.gloop.messenger.dialogs.ChatInfoDialog;
import io.gloop.messenger.model.Chat;
import io.gloop.messenger.model.UserInfo;
import io.gloop.permissions.GloopUser;

public class ListFragment extends Fragment {

    private Context context;
    private GloopUser owner;
    private ChatAdapter chatAdapter;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private UserInfo userInfo;

    public static ListFragment newInstance(UserInfo userinfo, GloopUser owner) {
        ListFragment f = new ListFragment();
        Bundle args = new Bundle();
        args.putSerializable("userinfo", userinfo);
        args.putSerializable("owner", owner);
        f.setArguments(args);
        return f;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final RelativeLayout rv = (RelativeLayout) inflater.inflate(R.layout.fragment_list, container, false);
        setHasOptionsMenu(true);

        Bundle args = getArguments();
        this.userInfo = (UserInfo) args.getSerializable("userinfo");
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
                chatAdapter.filter(s);
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
            GloopList<Chat> all = Gloop.all(Chat.class)
                    .where()
                    .equalsTo("user1", userInfo)
                    .or()
                    .equalsTo("user2", userInfo)
                    .all();

            all.load();
            return all;
        }


        @Override
        protected void onPostExecute(GloopList<Chat> tasks) {
            super.onPostExecute(tasks);
            try {
                chatAdapter = new ChatAdapter(tasks);
                recyclerView.setAdapter(chatAdapter);
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

    public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.BoardViewHolder> {

        private ArrayList<Chat> list;
        private final GloopList<Chat> originalList;

        ChatAdapter(final GloopList<Chat> tasks) {
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

            if (userInfo.getPhone().equals(chat.getUser1().getPhone()))
                holder.mContentView.setText(chat.getUser2().getUserName());
            else
                holder.mContentView.setText(chat.getUser1().getUserName());

//            final int color = task.getColor();
//            holder.mImage.setBackgroundColor(color);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra(ChatActivity.CHAT, chat);
                    intent.putExtra(ChatActivity.USER_INFO, userInfo);
                    context.startActivity(intent);
//                    progress.dismiss();
                }
            });

            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    int mWidth = getResources().getDisplayMetrics().widthPixels;
                    int mHeight = getResources().getDisplayMetrics().heightPixels;


                    new ChatInfoDialog(context, owner, chat, userInfo, mHeight / 2, mWidth / 2);
                    setupRecyclerView();
                    return true;
                }
            });
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