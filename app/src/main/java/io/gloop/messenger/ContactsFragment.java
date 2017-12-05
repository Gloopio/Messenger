package io.gloop.messenger;

import android.app.Activity;
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
import io.gloop.messenger.model.Chat;
import io.gloop.messenger.model.UserInfo;
import io.gloop.messenger.utils.ContactUtil;
import io.gloop.messenger.utils.Store;
import io.gloop.permissions.GloopGroup;
import io.gloop.permissions.GloopUser;

public class ContactsFragment extends Fragment {

    private final static String SELECTED = "selected";
    private final static String NOT_SELECTED = "notSelected";

    private UserInfoAdapter userInfoAdapter;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static ContactsFragment newInstance(int operation, UserInfo userinfo, GloopUser owner) {
        ContactsFragment f = new ContactsFragment();
//        Bundle args = new Bundle();
//        args.putInt("operation", operation);
//        args.putSerializable("userinfo", userinfo);
//        args.putSerializable("owner", owner);
//        f.setArguments(args);
        return f;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final RelativeLayout rv = (RelativeLayout) inflater.inflate(R.layout.fragment_list, container, false);
        setHasOptionsMenu(true);

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
                userInfoAdapter.filter(s);
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

    private class LoadTasksTask extends AsyncTask<Void, Integer, GloopList<UserInfo>> {
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
        protected GloopList<UserInfo> doInBackground(Void... urls) {
            Activity activity = getActivity();
//            while (activity == null)
//                activity = getActivity();

            GloopList<UserInfo> userInfos = ContactUtil.syncContacts(activity);

            if (userInfos != null)
                userInfos.load();
            return userInfos;
        }


        @Override
        protected void onPostExecute(GloopList<UserInfo> tasks) {
            super.onPostExecute(tasks);
            try {
                userInfoAdapter = new UserInfoAdapter(tasks);
                recyclerView.setAdapter(userInfoAdapter);
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

    public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.BoardViewHolder> {

        private ArrayList<UserInfo> list;
        private final GloopList<UserInfo> originalList;

        UserInfoAdapter(final GloopList<UserInfo> tasks) {
            originalList = tasks;
            list = (ArrayList<UserInfo>) tasks.getLocalCopy();
            Collections.sort(list, Collections.reverseOrder(new Comparator<UserInfo>() {
                @Override
                public int compare(UserInfo left, UserInfo right) {
                    return Long.compare(left.getTimestamp(), right.getTimestamp());
                }
            }));
        }

        @Override
        public BoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
            return new BoardViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final BoardViewHolder holder, int position) {
            final UserInfo userInfo = list.get(position);

            holder.mContentView.setText(userInfo.getUserName());
            holder.mPhoneNumber.setText(userInfo.getPhone());
//            final int color = task.getColor();
//            holder.mImage.setBackgroundColor(color);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    openChat(Store.getOwnerUserInfo(), userInfo);
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
                list = (ArrayList<UserInfo>) originalList.getLocalCopy();
            } else {
                String search = s.toLowerCase();
                for (UserInfo task : originalList) {
                    if (!task.getUserName().toLowerCase().startsWith(search))
                        list.remove(task);
                }
            }

            notifyDataSetChanged();
        }

        class BoardViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mContentView;
            final TextView mPhoneNumber;
            final ImageView mImage;

            final List<CircleImageView> memberImages = new ArrayList<>();


            BoardViewHolder(View view) {
                super(view);
                mView = view.findViewById(R.id.card_view);
                mContentView = (TextView) view.findViewById(R.id.board_name);
                mPhoneNumber = (TextView) view.findViewById(R.id.phone_number);
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

    private void openChat(UserInfo ownerUserInfo, UserInfo userInfo) {

        // check if there exists already a chat of these users.

        Chat existingChat = Gloop.all(Chat.class).where()
                .equalsTo("user1", ownerUserInfo).and().equalsTo("user1", userInfo)
                .or()
                .equalsTo("user1", userInfo).and().equalsTo("user1", ownerUserInfo)
                .first();

        if (existingChat != null) {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("chat", existingChat);
            getContext().startActivity(intent);
        } else {
            // create new chat

            GloopGroup group = new GloopGroup();
            group.addMember(ownerUserInfo.getPhone());
            group.addMember(userInfo.getPhone());
            group.save();

            // add permissions to group

            Chat chat = new Chat();
            chat.setUser1(ownerUserInfo);
            chat.setUser2(userInfo);
            chat.setUser(group.getObjectId());
            chat.save();

            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("chat", chat);
            getContext().startActivity(intent);

        }
    }
}