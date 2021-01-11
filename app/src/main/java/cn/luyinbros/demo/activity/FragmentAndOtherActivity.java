package cn.luyinbros.demo.activity;

import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import cn.luyinbros.valleyframework.controller.annotation.BindView;
import cn.luyinbros.valleyframework.controller.annotation.BuildView;
import cn.luyinbros.valleyframework.controller.annotation.Controller;
import cn.luyinbros.demo.R;
import cn.luyinbros.demo.base.BaseActivity;
import cn.luyinbros.demo.base.RecyclerViewItemController;
import cn.luyinbros.demo.controller.OnSingleClick;
import cn.luyinbros.demo.fragment.BundleValueFragment;
import cn.luyinbros.demo.fragment.EmptyFragment;
import cn.luyinbros.demo.fragment.OnActivityResultFragment;
import cn.luyinbros.demo.fragment.OnPermissionResultFragment;
import cn.luyinbros.demo.mock.Mock;

//@Controller(R.layout.activity_fragment_other)
public class FragmentAndOtherActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;


    @BuildView
    void onViewCreate() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new RecyclerViewAdapter());
        mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
    }


    private static class PagerAdapter extends FragmentPagerAdapter {

        private PagerAdapter(@NonNull FragmentManager fm) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                Fragment fragment = new BundleValueFragment();
                fragment.setArguments(Mock.testBundle());
                return fragment;
            } else if (position == 1) {
                return new OnActivityResultFragment();
            } else if (position == 2) {
                return new OnPermissionResultFragment();
            } else if (position == 3) {
                return new EmptyFragment();
            }
            throw new IllegalStateException("");
        }

        @Override
        public int getCount() {
            return 4;
        }
    }


    private static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TextHolder(parent).mHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final TextHolder textHolder = RecyclerViewItemController.as(holder);
            textHolder.nameTextView.setText("position: " + position);

        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }


   // @Controller(R.layout.list_item_text)
    static class TextHolder extends RecyclerViewItemController {
        @BindView(R.id.nameTextView)
        TextView nameTextView;


        private TextHolder(ViewGroup parent) {
            super(parent);
        }


        @OnSingleClick(R.id.nameTextView)
        void onNameClick() {
            Toast.makeText(mHolder.itemView.getContext(), "消息:" + getAdapterPosition(), Toast.LENGTH_SHORT).show();
        }


    }


}
