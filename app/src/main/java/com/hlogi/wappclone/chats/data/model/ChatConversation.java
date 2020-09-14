package com.hlogi.wappclone.chats.data.model;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;
import com.hlogi.wappclone.chats.data.DataChatConversationNames;

import java.util.List;
import java.util.Objects;

@Entity(tableName = DataChatConversationNames.TABLE_NAME)
public class ChatConversation {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = DataChatConversationNames.COL_CHAT_CONVERSATION_ID)
    private String id;

    @ColumnInfo(name = DataChatConversationNames.COL_TYPE)
    private String type;

    @ColumnInfo(name = DataChatConversationNames.COL_USERS)
    private List<String> users;

    private Long last_update;

    @Embedded
    private GroupConversation group;

//    @Exclude
//    @Embedded
//    private Message last_message;

    @Ignore
    public ChatConversation() {
    }

    public ChatConversation(@NonNull String id, String type, List<String> users, Long last_update, GroupConversation group) {
        this.id = id;
        this.type = type;
        this.users = users;
        this.last_update = last_update;
        this.group = group;
    }

//    public ChatConversation(@NonNull String id, String type, List<String> users, GroupConversation group) {
//        this.id = id;
//        this.type = type;
//        this.users = users;
//        this.group = group;
////        this.last_message = last_message;
//    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public GroupConversation getGroup() {
        return group;
    }

    public void setGroup(GroupConversation group) {
        this.group = group;
    }

    public Long getLast_update() {
        return last_update;
    }

    public void setLast_update(Long last_update) {
        this.last_update = last_update;
    }

    @Override
    public String toString() {
        return "ChatConversation{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", users=" + users +
                ", last_update=" + last_update +
                ", group=" + group +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatConversation)) return false;
        ChatConversation that = (ChatConversation) o;
        return getId().equals(that.getId()) &&
                Objects.equals(getType(), that.getType()) &&
                Objects.equals(getUsers(), that.getUsers()) &&
                Objects.equals(getLast_update(), that.getLast_update()) &&
                Objects.equals(getGroup(), that.getGroup());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getType(), getUsers(), getLast_update(), getGroup());
    }

    //    @Exclude
//    public Message getLast_message() {
//        return last_message;
//    }
//
//    @Exclude
//    public void setLast_message(Message last_message) {
//        this.last_message = last_message;
//    }

//    @Override
//    public String toString() {
//        return "ChatConversation{" +
//                "id='" + id + '\'' +
//                ", type='" + type + '\'' +
//                ", users=" + users +
//                ", group=" + group +
//                ", last_message=" + last_message +
//                '}';
//    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof ChatConversation)) return false;
//        ChatConversation that = (ChatConversation) o;
//        return getId().equals(that.getId()) &&
//                Objects.equals(getType(), that.getType()) &&
//                Objects.equals(getUsers(), that.getUsers()) &&
//                Objects.equals(getGroup(), that.getGroup()) &&
//                Objects.equals(getLast_message(), that.getLast_message());
//    }

//    @Override
//    public int hashCode() {
//        return Objects.hash(getId(), getType(), getUsers(), getGroup(), getLast_message());
//    }

    public static final DiffUtil.ItemCallback<ChatConversation> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ChatConversation>() {
                @Override
                public boolean areItemsTheSame(@NonNull ChatConversation oldItem, @NonNull ChatConversation newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull ChatConversation oldItem, @NonNull ChatConversation newItem) {
                    return oldItem.equals(newItem);
                }
            };

}
