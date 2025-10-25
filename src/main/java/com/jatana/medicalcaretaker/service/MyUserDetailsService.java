package com.jatana.medicalcaretaker.service;

import com.jatana.medicalcaretaker.model.User;
import com.jatana.medicalcaretaker.model.userEnums.Role;
import com.jatana.medicalcaretaker.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo repo;

    @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = repo.findByEmail(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new UserPrincipal(user);
        }

        throw new UsernameNotFoundException(username);

    }


    public User saveUser(User user) {
        // Check if email already exists
        if (user.getEmail() != null && repo.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        
        // Check if mobile number already exists
        if (user.getMobileNumber() != null && repo.findByMobileNumber(user.getMobileNumber()).isPresent()) {
            throw new IllegalArgumentException("Mobile number already exists: " + user.getMobileNumber());
        }
        
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        user.setPassword(encoder.encode(user.getPassword()));
        
        try {
            return repo.save(user);
        } catch (DuplicateKeyException e) {
            // MongoDB throws this when unique constraint is violated (backup check)
            String errorMessage = e.getMessage();
            if (errorMessage.contains("email")) {
                throw new IllegalArgumentException("Email already exists");
            } else if (errorMessage.contains("mobileNumber")) {
                throw new IllegalArgumentException("Mobile number already exists");
            } else {
                throw new IllegalArgumentException("Duplicate value detected");
            }
        }
    }

    public User addCaretaker(String userId, String caretakerEmail) {
        System.out.println("Attempting to add caretaker. User ID: " + userId + ", Caretaker Email: " + caretakerEmail);

        Optional<User> current = repo.findById(userId);
        if (current.isPresent()) {
            User currentUser = current.get();
            System.out.println("Found current user: " + currentUser.getEmail());

            if (currentUser.getRole() != Role.CARERECIEVER) {
                System.out.println("Error: Current user is not a CARERECIEVER. Role found: " + currentUser.getRole());
                throw new IllegalArgumentException("Current user is not a CARERECIEVER");
            }

            Optional<User> user = repo.findByEmail(caretakerEmail);
            if (user.isPresent()) {
                User careTaker = user.get();
                System.out.println("Found caretaker user: " + careTaker.getEmail());

                if (careTaker.getRole() != Role.CARETAKER) {
                    System.out.println("Error: Provided user is not a CARETAKER. Role found: " + careTaker.getRole());
                    throw new IllegalArgumentException("User given for caretaking is not CARETAKER");
                }

                System.out.println("Adding caretaker relationship...");
                Set<String> careTakerOfCurrentUser = new HashSet<>(currentUser.getCareTakerUserIds());
                careTakerOfCurrentUser.add(careTaker.getId());

                Set<String> careReceiverOfCaretaker = new HashSet<>(careTaker.getCareReceiverUserIds());
                careReceiverOfCaretaker.add(currentUser.getId());

                currentUser.setCareTakerUserIds(new ArrayList<>(careTakerOfCurrentUser));
                careTaker.setCareReceiverUserIds(new ArrayList<>(careReceiverOfCaretaker));

                repo.save(careTaker);
                System.out.println("Saved caretaker user: " + careTaker.getEmail());

                User savedUser = repo.save(currentUser);
                System.out.println("Saved current user with updated caretaker: " + savedUser.getEmail());

                return savedUser;
            }

            System.out.println("Error: Caretaker not found with email: " + caretakerEmail);
            throw new IllegalArgumentException("Caretaker not found");
        }

        System.out.println("Error: User not found with ID: " + userId);
        throw new IllegalArgumentException("Caretaker not found");
    }


    /**
     * Get the authenticated user's ID from the security context
     * 
     * @return The ID of the currently authenticated user
     */
    public String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getUserId();
    }

    /**
     * Check if the actor user has permission to access the target user's data.
     * Permission is granted if:
     * 1. Actor is the same as target user (accessing own data)
     * 2. Actor is an ADMIN
     * 3. Actor is a caretaker of the target user
     * 
     * @param actorUserId The ID of the user performing the action
     * @param targetUserId The ID of the user whose data is being accessed
     * @throws AccessDeniedException if actor doesn't have permission
     */
    public void authorizeAccess(String actorUserId, String targetUserId) {
        // If actor is accessing their own data, allow it
        if (actorUserId.equals(targetUserId)) {
            return;
        }

        // Get actor user to check role and relationships
        Optional<User> actorUserOpt = repo.findById(actorUserId);
        if (actorUserOpt.isEmpty()) {
            throw new AccessDeniedException("Actor user not found");
        }
        
        User actorUser = actorUserOpt.get();
        
        // If actor is an ADMIN, allow access
        if (actorUser.getRole() == Role.ADMIN) {
            return;
        }

        // Get target user to check if actor is in their caretaker list
        Optional<User> targetUserOpt = repo.findById(targetUserId);
        if (targetUserOpt.isEmpty()) {
            throw new AccessDeniedException("Target user xnot found");
        }
        
        User targetUser = targetUserOpt.get();
        
        // Check if actor is a caretaker of the target user
        if (targetUser.getCareTakerUserIds() != null && 
            targetUser.getCareTakerUserIds().contains(actorUserId)) {
            return;
        }

        // If none of the conditions are met, deny access
        throw new AccessDeniedException("You do not have permission to access this user's data");
    }

}
