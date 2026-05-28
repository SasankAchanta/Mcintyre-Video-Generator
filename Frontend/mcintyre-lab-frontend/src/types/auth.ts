export type Role = 'ADMIN' | 'RESEARCHER' | 'TECH';

export interface User {
    userId?: string;
    firstName: string;
    lastName: string;
    profilePicture?: string;
    email: string;
    username: string;
    password?: string;
    role: Role;
    lastPasswordChangeAt?: string;
    createdAt?: string;
    updatedAt?: string;
}