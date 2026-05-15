export type UserScope = 'normal' | 'admin';

export interface UserLoginPayload {
  birthDate: string;
  password: string;
}

export interface NormalUserResponse {
  userId: string | null;
  birthDate: string;
  createdAt: string;
}

export interface AdminUserResponse {
  userId: string | null;
  birthDate: string;
  active: boolean;
  createdAt: string;
}

export type AuthenticatedUser = {
  scope: UserScope;
  userId: string | null;
  birthDate: string;
  createdAt: string;
  active?: boolean;
};

export type LoginResult = {
  user: AuthenticatedUser;
  created: boolean;
};
