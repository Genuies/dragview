package zx.com.genius.cn.pointview.listener;

/**
 * @author: Genius on 2018/11/9
 * @package: zx.com.genius.cn.pointview.listener
 * @function:
 */
public interface DragListener {

    /**
     * 拖拽的点回复到原位事件
     */
    void reset();

    /**
     * 拖拽的点消失事件
     */
    void disappear();

}
