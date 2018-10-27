package top.grapedge.grapeschat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import top.grapedge.qqlogin.R;

public class FriendsFragment extends BaseFragment {
    // 好友组列表
    private List<String> groupList = new ArrayList<>();
    // 子项列表
    private List<List<UserJsonData>> itemList = new ArrayList<List<UserJsonData>>();
    private ExpandableListView expandableListView;
    private FriendsListAdapter friendsListAdapter;

    @Nullable
    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 加载布局
        View friendsLayout = inflater.inflate(R.layout.fragment_friends, container, false);
        // ExpandableListView
        // 添加四个分组，其实我在加好友时没写分组功能，这里只是为了好看
        groupList.add("我的好友");
        groupList.add("我的同学");
        groupList.add("我的家人");
        groupList.add("黑名单");
        itemList.add(new ArrayList<UserJsonData>());
        itemList.add(new ArrayList<UserJsonData>());
        itemList.add(new ArrayList<UserJsonData>());
        itemList.add(new ArrayList<UserJsonData>());
        // find
        expandableListView = friendsLayout.findViewById(R.id.expand_list_friends);
        // 组点击
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (itemList.get(groupPosition) == null || itemList.get(groupPosition).isEmpty()) {
                    return true;
                }
                return false;
            }
        });
        // 子项点击
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(getContext(), "你选择了" + itemList.get(groupPosition).get(childPosition) + "用户", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        // 设置适配器
        friendsListAdapter = new FriendsListAdapter(this.getContext());
        expandableListView.setAdapter(friendsListAdapter);
        // 设置取消官方的那个箭头
        expandableListView.setGroupIndicator(null);
        // 点击添加好友按钮
        Button addFriendButton = friendsLayout.findViewById(R.id.add_friend);
        // 获得登录用户的数据
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                // 传入数据
                intent.putExtra(EXTRA_USER_DATA, loginUserJson);
                startActivity(intent);
            }
        });
        return friendsLayout;
    }

    // 更新好友列表
    @Override
    public void onResume() {
        super.onResume();
        getFriendList();
    }

    // 得到好友列表
    public void getFriendList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JsonData jsonData = new Gson().fromJson(HttpUtil.Get(HttpUtil.GET_FRIENDS_URL + "?id=" + loginUser.getId()), JsonData.class);
                String obj = jsonData.getObj();
                List<UserJsonData> friends = new Gson().fromJson(obj, new TypeToken<List<UserJsonData>>(){}.getType());
                itemList.set(0, friends);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        friendsListAdapter.updateList();
                    }
                });
            }
        }).start();
    }

    class FriendsListAdapter extends BaseExpandableListAdapter {
        private Context context;
        // 随机数，用于头像颜色随机
        private Random random = new Random();

        public FriendsListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getGroupCount() {
            return  groupList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return itemList.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return itemList.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        public void updateList() {
            notifyDataSetChanged();
        }

        // 和 ListView 相似
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupHolder groupHold;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.expand_list_group, parent, false);
                groupHold = new GroupHolder();
                groupHold.groupName = convertView.findViewById(R.id.group_name);
                groupHold.groupExpand = convertView.findViewById(R.id.group_expand);
                groupHold.groupCount = convertView.findViewById(R.id.group_count);
                convertView.setTag(groupHold);
            } else {
                groupHold = (GroupHolder) convertView.getTag();
            }

            groupHold.groupExpand.setText(isExpanded ? "∨" : "﹥");
            groupHold.groupName.setText(groupList.get(groupPosition));
            groupHold.groupCount.setText(String.valueOf(getChildrenCount(groupPosition)));

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ItemHolder itemHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.expand_list_item, parent, false);
                itemHolder = new ItemHolder();
                itemHolder.friendAvatar = convertView.findViewById(R.id.friend_avatar);
                itemHolder.friendName = convertView.findViewById(R.id.friend_name);
                convertView.setTag(itemHolder);
            } else {
                itemHolder = (ItemHolder)convertView.getTag();
            }
            itemHolder.friendName.setText(itemList.get(groupPosition).get(childPosition).getNickname());
            itemHolder.friendAvatar.setBackgroundColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        class GroupHolder {
            public TextView groupName;
            public TextView groupExpand;
            public TextView groupCount;
        }

        class ItemHolder {
            public TextView friendName;
            public ImageView friendAvatar;
        }

    }

}
