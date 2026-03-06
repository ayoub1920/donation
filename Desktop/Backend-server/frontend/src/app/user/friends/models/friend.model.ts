export interface Friend {
  id: number;
  name: string;
  avatar: string;
  lastMessage: string;
  time: string;
  online: boolean;
  status?: string;
}

export interface FriendRequest {
  id: number;
  name: string;
  avatar: string;
  info: string;
}

export interface ChatMessage {
  id: number;
  senderId: number;
  text: string;
  time: string;
  isMine: boolean;
}
