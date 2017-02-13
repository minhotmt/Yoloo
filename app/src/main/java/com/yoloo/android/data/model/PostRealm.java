package com.yoloo.android.data.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostRealm extends RealmObject {

  @PrimaryKey private String id;
  @Index private String ownerId;
  private String avatarUrl;
  private String username;
  private String content;
  private int bounty;
  private String mediaUrl;
  private RealmList<TagRealm> tags;
  private RealmList<CategoryRealm> categories;
  @Index private boolean commented;
  @Index private String acceptedCommentId;
  @Index private Date created;
  private int dir;
  private long votes;
  private long comments;
  private int reports;
  private int type;
  private String title;
  private double rank;
  private String categoriesAsString;
  @Index private boolean isFeedItem;
  @Index private boolean pending;
  @Index private boolean bookmarked;

  /*public PostRealm(FeedItem feedItem) {
  }*/

  public PostRealm() {
  }

  public String getId() {
    return id;
  }

  public PostRealm setId(String id) {
    this.id = id;
    return this;
  }

  public String getOwnerId() {
    return ownerId;
  }

  public PostRealm setOwnerId(String ownerId) {
    this.ownerId = ownerId;
    return this;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public PostRealm setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
    return this;
  }

  public String getUsername() {
    return username;
  }

  public PostRealm setUsername(String username) {
    this.username = username;
    return this;
  }

  public String getContent() {
    return content;
  }

  public PostRealm setContent(String content) {
    this.content = content;
    return this;
  }

  public int getBounty() {
    return bounty;
  }

  public PostRealm setBounty(int bounty) {
    this.bounty = bounty;
    return this;
  }

  public String getMediaUrl() {
    return mediaUrl;
  }

  public PostRealm setMediaUrl(String mediaUrl) {
    this.mediaUrl = mediaUrl;
    return this;
  }

  public RealmList<TagRealm> getTags() {
    return tags;
  }

  public PostRealm setTags(RealmList<TagRealm> tags) {
    this.tags = tags;
    return this;
  }

  public RealmList<CategoryRealm> getCategories() {
    return categories;
  }

  public PostRealm setCategories(RealmList<CategoryRealm> categories) {
    this.categories = categories;
    return this;
  }

  public List<String> getCategoryNames() {
    List<String> names = new ArrayList<>(3);
    for (CategoryRealm category : categories) {
      names.add(category.getName());
    }

    return names;
  }

  public PostRealm addCategory(CategoryRealm categoryRealm) {
    this.categories.add(categoryRealm);
    return this;
  }

  public boolean isCommented() {
    return commented;
  }

  public PostRealm setCommented(boolean commented) {
    this.commented = commented;
    return this;
  }

  public String getAcceptedCommentId() {
    return acceptedCommentId;
  }

  public PostRealm setAcceptedCommentId(String acceptedCommentId) {
    this.acceptedCommentId = acceptedCommentId;
    return this;
  }

  public Date getCreated() {
    return created;
  }

  public PostRealm setCreated(Date created) {
    this.created = created;
    return this;
  }

  public int getDir() {
    return dir;
  }

  public PostRealm setDir(int dir) {
    this.dir = dir;
    return this;
  }

  public long getVotes() {
    return votes;
  }

  public PostRealm setVotes(long votes) {
    this.votes = votes;
    return this;
  }

  public long getComments() {
    return comments;
  }

  public PostRealm setComments(long comments) {
    this.comments = comments;
    return this;
  }

  public int getReports() {
    return reports;
  }

  public PostRealm setReports(int reports) {
    this.reports = reports;
    return this;
  }

  public int getType() {
    return type;
  }

  public PostRealm setType(int type) {
    this.type = type;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public PostRealm setTitle(String title) {
    this.title = title;
    return this;
  }

  public double getRank() {
    return rank;
  }

  public PostRealm setRank(double rank) {
    this.rank = rank;
    return this;
  }

  public String getCategoriesAsString() {
    return categoriesAsString;
  }

  public PostRealm setCategoriesAsString(String categoriesAsString) {
    this.categoriesAsString = categoriesAsString;
    return this;
  }

  public boolean isFeedItem() {
    return isFeedItem;
  }

  public PostRealm setFeedItem(boolean feedItem) {
    isFeedItem = feedItem;
    return this;
  }

  public boolean isPending() {
    return pending;
  }

  public PostRealm setPending(boolean pending) {
    this.pending = pending;
    return this;
  }

  public boolean isBookmarked() {
    return bookmarked;
  }

  public PostRealm setBookmarked(boolean bookmarked) {
    this.bookmarked = bookmarked;
    return this;
  }

  public void increaseVotes() {
    ++this.votes;
  }

  public void decreaseVotes() {
    --this.votes;
  }

  public void increaseComments() {
    ++this.comments;
  }

  public void decreaseComments() {
    --this.comments;
  }

  public boolean hasReadMore() {
    return content.length() >= 200;
  }

  @Override public String toString() {
    return "PostRealm{"
        + "id='"
        + id
        + '\''
        + ", ownerId='"
        + ownerId
        + '\''
        + ", avatarUrl='"
        + avatarUrl
        + '\''
        + ", username='"
        + username
        + '\''
        + ", content='"
        + content
        + '\''
        + ", bounty="
        + bounty
        + ", mediaUrl='"
        + mediaUrl
        + '\''
        + ", tags="
        + tags
        + ", categories="
        + categories
        + ", commented="
        + commented
        + ", acceptedCommentId='"
        + acceptedCommentId
        + '\''
        + ", created="
        + created
        + ", dir="
        + dir
        + ", votes="
        + votes
        + ", comments="
        + comments
        + ", reports="
        + reports
        + ", type="
        + type
        + ", title='"
        + title
        + '\''
        + ", rank="
        + rank
        + ", categoriesAsString='"
        + categoriesAsString
        + '\''
        + ", isFeedItem="
        + isFeedItem
        + ", pending="
        + pending
        + ", bookmarked="
        + bookmarked
        + '}';
  }
}