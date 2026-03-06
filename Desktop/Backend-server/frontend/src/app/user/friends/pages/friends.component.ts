import { Component } from '@angular/core';
import { MOCK_FRIENDS, MOCK_FRIEND_REQUESTS, MOCK_CHAT_MESSAGES, MOCK_USER } from '../../../shared/constants/mock-data';

@Component({
  selector: 'app-friends',
  standalone: true,
  templateUrl: './friends.component.html'
})
export class FriendsComponent {
  friends = MOCK_FRIENDS;
  friendRequests = MOCK_FRIEND_REQUESTS;
  chatMessages = MOCK_CHAT_MESSAGES;
  user = MOCK_USER;
  selectedFriend = MOCK_FRIENDS[0];
  searchQuery = '';

  get onlineFriends() {
    return this.friends.filter(f => f.online);
  }

  selectFriend(friend: any) {
    this.selectedFriend = friend;
  }
}
