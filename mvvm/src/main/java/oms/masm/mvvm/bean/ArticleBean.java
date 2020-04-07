package oms.masm.mvvm.bean;

import androidx.databinding.ObservableField;

/**
 * NAME: 柚子啊
 * DATE: 2020/4/6
 * DESC:
 */

public class ArticleBean {

    private int courseId;
    private int id;
//    private String name;
    public ObservableField<String> name = new ObservableField<>();
    private int order;
    private int parentChapterId;
    private boolean userControlSetTop;
    private int visible;


    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    public ObservableField<String> getName() {
//        return name;
//    }
//
//    public void setName(ObservableField<String> name) {
//        this.name = name;
//    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getParentChapterId() {
        return parentChapterId;
    }

    public void setParentChapterId(int parentChapterId) {
        this.parentChapterId = parentChapterId;
    }

    public boolean isUserControlSetTop() {
        return userControlSetTop;
    }

    public void setUserControlSetTop(boolean userControlSetTop) {
        this.userControlSetTop = userControlSetTop;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

}
