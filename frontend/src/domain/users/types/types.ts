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

export type AdminUserResponse = NormalUserResponse;

export type AuthSessionUser = NormalUserResponse & {
  scope: UserScope;
};

export interface LoginSubmitResult {
  created: boolean;
}
