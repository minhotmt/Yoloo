package com.yoloo.android.data.repository.chat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yoloo.android.rxfirebase.FirebaseChildEvent;
import com.yoloo.android.rxfirebase.RxFirebaseDatabase;
import io.reactivex.Observable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public class ChatRepository {

  private static ChatRepository instance;

  private DatabaseReference rootRef;
  private DatabaseReference usersRef;
  private DatabaseReference chatsRef;
  private DatabaseReference messagesRef;
  private DatabaseReference membersRef;

  private RxFirebaseDatabase rxFirebaseDb;

  private ChatRepository() {
    rootRef = FirebaseDatabase.getInstance().getReference();
    usersRef = rootRef.child("users");
    chatsRef = rootRef.child("chats");
    messagesRef = rootRef.child("messages");
    membersRef = rootRef.child("members");
    rxFirebaseDb = RxFirebaseDatabase.getInstance();
  }

  public static ChatRepository getInstance() {
    if (instance == null) {
      instance = new ChatRepository();
    }
    return instance;
  }

  /*public Chat addDialog(@Nonnull Chat chat) {
    Map<String, Object> update = new HashMap<>();

    final String dialogId = rootRef.push().getKey();

    chat.setId(dialogId);
    // chats/$chatId/meta
    update.put("chats/" + dialogId + "/meta", chat);

    // members/$chatId/$userId
    Stream.of(chat.getMembers()).forEach(user ->
        update.put("members/" + dialogId + "/" + user.getKey(), user.getValue().getRole()));

    // users/$userId/$chatId
    Stream.of(chat.getMembers()).forEach(user ->
        update.put("users/" + user.getKey() + "/chats/" + dialogId, user.getValue().getRole()));

    rootRef.updateChildren(update);

    return chat.setId(dialogId);
  }*/

  /*public void deleteDialog(@Nonnull Chat chat) {
    Map<String, Object> delete = new HashMap<>();
    delete.put("members/" + chat.getId(), null);
    delete.put("chats/" + chat.getId(), null);
    delete.put("messages/" + chat.getId(), null);
    Stream.of(chat.getMembers()).forEach(user ->
        delete.put("users/" + user.getKey() + "/chats/" + chat.getId(), null));

    rootRef.updateChildren(delete);
  }*/

  public Observable<FirebaseChildEvent> getMessagesByDialogId(@Nonnull String dialogId) {
    return rxFirebaseDb.observeChildEvent(messagesRef.child(dialogId));
  }

  /*public void addMessage(@Nonnull ChatMessage message) {
    final String messageId = rootRef.push().getKey();
    Map<String, Object> update = new HashMap<>();
    update.put("messages/" + message.getDialogId() + "/" + messageId, message);
    update.put("chats/" + message.getDialogId() + "/meta/lastSenderId", message.getUser().getId());
    update.put("chats/" + message.getDialogId() + "/meta/lastMessage",
        getTrimmedText(message.getMessage()));
    update.put("chats/" + message.getDialogId() + "/meta/updated", ServerValue.TIMESTAMP);

    rootRef.updateChildren(update);

    //messagesRef.child(message.getDialogId()).child(messageId).setValue(message);
  }*/

  /*public void deleteMessage(@Nonnull ChatMessage message) {
    Map<String, Object> update = new HashMap<>();
    update.put(message.getDialogId() + "/" + message.getId(), null);

    messagesRef.updateChildren(update);
  }*/

  public boolean isDialogExists(@Nonnull String dialogId, @Nonnull String userId) {

    return true;
  }

  public void exitGroup(@Nonnull String dialogId, @Nonnull String userIdToRemove) {
    Map<String, Object> delete = new HashMap<>();
    delete.put("chats/" + dialogId + "/meta/members/" + userIdToRemove, null);
    delete.put("members/" + dialogId + "/" + userIdToRemove, null);
    delete.put("users/" + userIdToRemove + "/chats/" + dialogId, null);
    rootRef.updateChildren(delete);
  }

  public Observable<FirebaseChildEvent> listDialogsByUserId(@Nonnull String userId) {
    return rxFirebaseDb
        .observeChildEvent(usersRef.child(userId).child("chats"))
        .map(event -> event.getDataSnapshot().getKey())
        .flatMap(dialogId -> rxFirebaseDb.observeChildEvent(chatsRef.child(dialogId)));
  }

  private String getTrimmedText(String content) {
    return content.length() > 38
        ? content.substring(0, 38).concat("...")
        : content;
  }
}
