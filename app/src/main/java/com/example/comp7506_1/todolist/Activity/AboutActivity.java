package com.example.comp7506_1.todolist.Activity;

import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.comp7506_1.todolist.BuildConfig;
import com.example.comp7506_1.todolist.R;

import me.drakeet.multitype.Items;
import me.drakeet.support.about.AbsAboutActivity;
import me.drakeet.support.about.Card;
import me.drakeet.support.about.Category;
import me.drakeet.support.about.Contributor;

public class AboutActivity extends AbsAboutActivity {

    @Override
    protected void onCreateHeader(@NonNull ImageView icon, @NonNull TextView slogan, @NonNull TextView version) {
        icon.setImageResource(R.drawable.ic_launcher_white);
        slogan.setText("Tomato to the struggle, Potato to the life");
    }


    @Override
    protected void onItemsCreated(@NonNull Items items) {
        items.add(new Category("Introduction"));
        items.add(new Card(getString(R.string.about_app)));

        items.add(new Category("features"));
        items.add(new Card(getString(R.string.about_function)));

        items.add(new Category("developers"));
        items.add(new Contributor(R.drawable.designer1,"Ren Yuan", "Developer & designer", ""));
        items.add(new Contributor(R.drawable.designer2,"Xu Xuyang", "Developer & designer",""));
        items.add(new Contributor(R.drawable.designer3,"Zeng Yuxuan", "Developer & designer",""));


    }
}
