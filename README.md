# dragview
a draggable view for android, like the QQ message point

First, let's see the gif

![image](https://github.com/Genuies/dragview/blob/master/2.gif)

it's easy to use, in your layout file:
```
<zx.com.genius.cn.pointview.DragView
            android:id="@+id/person_drag_one"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="10sp"
            android:textColor="#ffffff"
            android:layout_alignParentEnd="true"
            android:layout_centerVertical="true"
            android:layout_marginEnd="10dp"
            android:text="@string/person_msg_one"
            app:auto_fit="true"
            app:big_circle="25"
            app:small_circle="15"
            app:back_color="@color/colorAccent"/>
```

the DragView extends TextView. It has four attributes:
* auto_fit: wether the dragview adapt text width
* big_circle: the big circle raduis
* small_circle: the small circle raduis
* back_color: the circle color

about the animation, I use [this](https://github.com/835127729/ViewExplosion)

To use this, int the gradle:

Step 1. Add the JitPack repository to your build file
```
allprojects {
    repositories {
	...
	maven { url 'https://jitpack.io' }
    }
}
```
Step 2. Add the dependency
```
dependencies {
    implementation 'com.github.Genuies:dragview:1.0'
}
```

