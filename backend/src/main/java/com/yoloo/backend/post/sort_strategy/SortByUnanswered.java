package com.yoloo.backend.post.sort_strategy;

import com.googlecode.objectify.cmd.Query;
import com.yoloo.backend.post.PostEntity;
import com.yoloo.backend.post.PostEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
final class SortByUnanswered implements PostSorter.SortStrategy {

  private final Query<PostEntity> query;

  @Override
  public Query<PostEntity> sort() {
    return query.filter(PostEntity.FIELD_COMMENTED + " =", false);
  }
}
