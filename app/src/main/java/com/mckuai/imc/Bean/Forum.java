package com.mckuai.imc.Bean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "Forum".
 */
public class Forum {

    private Long id;
    private Integer postCount;
    private String name;
    private String cover;
    private String type;

    public Forum() {
    }

    public Forum(Long id) {
        this.id = id;
    }

    public Forum(Long id, Integer postCount, String name, String cover, String type) {
        this.id = id;
        this.postCount = postCount;
        this.name = name;
        this.cover = cover;
        this.type = type;
    }

    public Forum(ForumInfo forumInfo){
        if (null != forumInfo){
            this.id = (long)forumInfo.getId();
            this.postCount = forumInfo.getTalkNum();
            this.name = forumInfo.getName();
            this.cover = forumInfo.getIcon();
            if (null != forumInfo.getIncludeType() && !forumInfo.getIncludeType().isEmpty()){
                for (PostType postType:forumInfo.getIncludeType()){
                    this.type += (postType.getId()+"|");
                }
                this.type = this.type.substring(0,this.type.length() - 2);
            }
        }
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPostCount() {
        return postCount;
    }

    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ForumInfo toForumInfo(){
        ForumInfo forumInfo = new ForumInfo();
        forumInfo.setId(this.id.intValue());
        forumInfo.setName(this.name);
        forumInfo.setIcon(this.cover);
        forumInfo.setTalkNum(this.postCount);
        return new ForumInfo();
    }

}
