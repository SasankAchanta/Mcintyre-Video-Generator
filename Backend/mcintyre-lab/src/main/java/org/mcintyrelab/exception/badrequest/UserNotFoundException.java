package org.mcintyrelab.exception.badrequest;

public class UserNotFoundException extends ResourceNotFoundException{
    public UserNotFoundException(String identifier) {
        super("No user account found with identifier: '" + identifier + "'");
    }
}
