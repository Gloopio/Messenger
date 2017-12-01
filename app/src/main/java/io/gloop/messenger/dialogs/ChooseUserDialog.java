package io.gloop.messenger.dialogs;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import io.gloop.GloopLogger;
import io.gloop.messenger.ChatActivity;
import io.gloop.messenger.R;
import io.gloop.messenger.model.Chat;
import io.gloop.messenger.model.UserInfo;
import io.gloop.permissions.GloopGroup;

/**
 * Created by Alex Untertrifaller on 21.09.17.
 */

public class ChooseUserDialog {

    private Context context;
    private MyRecyclerViewAdapter adapter;

    public ChooseUserDialog(Context context, final UserInfo userInfo) {
        this.context = context;
        final View dialogView = View.inflate(context, R.layout.dialog_choose_user, null);

        final Dialog dialog = new Dialog(context, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        ImageView imageView = (ImageView) dialog.findViewById(R.id.closeDialogImg);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                revealShow(dialogView, false, dialog);
            }
        });

        final AutoCompleteTextView newMember = (AutoCompleteTextView) dialog.findViewById(R.id.member_new);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        } else {
            ContentResolver content = context.getContentResolver();
            Cursor cursor = content.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null, null, null);
            final ContactListAdapter adapter = new ContactListAdapter(context, cursor, true);
            newMember.setThreshold(0);
            newMember.setAdapter(adapter);
        }

        Button chooseUserButton = (Button) dialog.findViewById(R.id.user_choose);
        chooseUserButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {


                String memberId = newMember.getText().toString();

                GloopGroup group = new GloopGroup();
                group.addMember(memberId);
                group.addMember(userInfo.getPhone());
                group.save();

                // add permissions to group

                Chat chat = new Chat();
                chat.setUser1(userInfo);
                chat.setUser(group.getObjectId());
                chat.save();

                // TODO load UserInfo of choosen user and add;
//                chat.setUser2(new UserInfo());

//                if (!memberId.equals("")) {
//                    group.addMember(memberId);
//                    group.save();
//                    adapter.notifyDataSetChanged();
//                    newMember.setText("");
//                }


                Context context = view.getContext();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("chat", chat);
//                    intent.putExtra(TaskDetailFragment.ARG_BOARD, task);
//                    intent.putExtra(TaskDetailFragment.ARG_USER_INFO, userInfo);
                context.startActivity(intent);
                revealShow(dialogView, false, dialog);
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                revealShow(dialogView, true, null);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {

                    revealShow(dialogView, false, dialog);
                    return true;
                }

                return false;
            }
        });

        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

        private List<String> mData = Collections.emptyList();
        private LayoutInflater mInflater;
        private GloopGroup group;

        // data is passed into the constructor
        MyRecyclerViewAdapter(Context context, List<String> data, GloopGroup group) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
            this.group = group;
        }

        // inflates the row layout from xml when needed
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item_member, parent, false);
            return new ViewHolder(view);
        }

        // binds the data to the textview in each row
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final String memberId = mData.get(position);
            holder.myTextView.setText(memberId);
            holder.removeMember.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    group.getMembers().remove(memberId);
                    group.save();
                    adapter.notifyDataSetChanged();
                    holder.myTextView.setText("");
                }
            });
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
        }


        // stores and recycles views as they are scrolled off screen
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView myTextView;
            ImageView removeMember;

            ViewHolder(View itemView) {
                super(itemView);
                myTextView = (TextView) itemView.findViewById(R.id.tvAnimalName);
                removeMember = (ImageView) itemView.findViewById(R.id.member_remove);
            }
        }
    }

    private void revealShow(View dialogView, boolean b, final Dialog dialog) {

        final View view = dialogView.findViewById(R.id.dialog);

        int w = view.getWidth();
        int h = view.getHeight();

        int endRadius = (int) Math.hypot(w, h);

        int mWidth = context.getResources().getDisplayMetrics().widthPixels;
        int mHeight = context.getResources().getDisplayMetrics().heightPixels;

        int cx = mWidth / 2;
        int cy = mHeight / 2;


        if (b) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, endRadius);

            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(700);
            revealAnimator.start();

        } else {

            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(700);
            anim.start();
        }
    }

    private static void doRestart(Context c) {
        try {
            //check if the context is given
            if (c != null) {
                //fetch the packagemanager so we can get the default launch activity
                // (you can replace this intent with any other activity if you want
                PackageManager pm = c.getPackageManager();
                //check if we got the PackageManager
                if (pm != null) {
                    //create the intent with the default start activity for your application
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                            c.getPackageName()
                    );
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //create a pending intent so the application is restarted after System.exit(0) was called.
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(c, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10, mPendingIntent);
                        //kill the application
                        System.exit(0);
                    } else {
                        GloopLogger.e("Was not able to restart application, mStartActivity null");
                    }
                } else {
                    GloopLogger.e("Was not able to restart application, PM null");
                }
            } else {
                GloopLogger.e("Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
            GloopLogger.e("Was not able to restart application");
        }
    }

    class ContactListAdapter extends CursorAdapter implements Filterable {
        private ContentResolver mCR;

        ContactListAdapter(Context context, Cursor c, boolean a) {
            super(context, c, true);
            mCR = context.getContentResolver();
        }


        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            final int emailIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
            ((TextView) view).setText(cursor.getString(emailIndex));
        }


        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final TextView view = (TextView) inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);


            final int emailIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
            view.setText(cursor.getString(emailIndex));

            return view;

        }

        @Override
        public String convertToString(Cursor cursor) {
            final int emailIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
            return cursor.getString(emailIndex);
        }

        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            if (getFilterQueryProvider() != null) {
                return getFilterQueryProvider().runQuery(constraint);
            }

            String query = constraint.toString();

            final String selection = ContactsContract.Contacts.DISPLAY_NAME
                    + " LIKE ? "
                    + " OR "
                    + ContactsContract.CommonDataKinds.Email.ADDRESS
                    + " LIKE ? ";

            String[] selectionArgs = new String[]{"%" + query + "%"
                    , "%" + query + "%"};

            return mCR.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, selection, selectionArgs, null);

        }

    }
}
