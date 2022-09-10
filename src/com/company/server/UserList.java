package com.company.server;

import java.util.ArrayList;

public class UserList {
    private ArrayList<ServerThread> curUsers;

    public UserList(){
        curUsers = new ArrayList<>();
    }

    public boolean isOnline(String name){
        synchronized (curUsers){
            for(ServerThread user : curUsers){
                if(user.getUsername().equals(name)){
                    return true;
                }
            }
            return false;
        }
    }

    public void addUser(ServerThread user){
        synchronized (curUsers){
            curUsers.add(user);
        }
    }

    public void removeUser(ServerThread user){
        synchronized (curUsers){
            curUsers.remove(user);
        }
    }

    public ArrayList<ServerThread> getUserList(){
        return curUsers;
    }
}
