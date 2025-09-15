package com.shelfconnect.service.impl;

import com.shelfconnect.Exception.APIException;
import com.shelfconnect.Exception.AppException;
import com.shelfconnect.dto.SharedContactDetailsDTO;
import com.shelfconnect.dto.req.UpdateProfileReq;
import com.shelfconnect.dto.res.PageRes;
import com.shelfconnect.model.Address;
import com.shelfconnect.model.Image;
import com.shelfconnect.model.SharedContactDetails;
import com.shelfconnect.model.User;
import com.shelfconnect.repo.SharedContactDetailsRepository;
import com.shelfconnect.repo.UserRepository;
import com.shelfconnect.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {
    private static final String AVATAR_FOLDER = "avatar";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final SharedContactDetailsRepository sharedContactDetailsRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ImageService imageService, SharedContactDetailsRepository sharedContactDetailsRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.imageService = imageService;
        this.sharedContactDetailsRepository = sharedContactDetailsRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return Optional.ofNullable(userRepository.findUserByEmail(username));
    }

    @Override
    public Optional<User> createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return Optional.of(userRepository.save(user));
    }

    @Override
    public Optional<User> updateUser(Long id, UpdateProfileReq updatedUser) {
        System.out.println(updatedUser.getName());
        User user = userRepository.findById(id).orElseThrow();
        Optional.ofNullable(updatedUser.getName()).ifPresent(user::setName);
        Optional.ofNullable(updatedUser.getEmail()).ifPresent(user::setEmail);
        Optional.ofNullable(updatedUser.getPhoneNo()).ifPresent(user::setPhone_no);
        Map<Long, Address> existingAddresses = user
                .getAddresses()
                .stream()
                .collect(Collectors.toMap(Address::getId, address -> address));
        if (updatedUser.getAddresses() != null)
            updatedUser.getAddresses()
                    .forEach(addressDTO -> {
                        if (addressDTO.getId() != null) {
                            if (existingAddresses.containsKey(addressDTO.getId())) {
                                Address existingAddress = existingAddresses.get(addressDTO.getId());
                                existingAddress.setCity(addressDTO.getCity() != null ? addressDTO.getCity() : existingAddress.getCity());
                                existingAddress.setState(addressDTO.getState() != null ? addressDTO.getState() : existingAddress.getState());
                                existingAddress.setArea(addressDTO.getArea() != null ? addressDTO.getArea() : existingAddress.getArea());
                                existingAddress.setCountry(addressDTO.getCountry() != null ? addressDTO.getCountry() : existingAddress.getCountry());
                                existingAddress.setPincode(addressDTO.getPincode() != null ? addressDTO.getPincode() : existingAddress.getPincode());
                            }
                        } else {
                            Address newAddress = Address.builder()
                                    .area(addressDTO.getArea())
                                    .city(addressDTO.getCity())
                                    .state(addressDTO.getState())
                                    .country(addressDTO.getCountry())
                                    .pincode(addressDTO.getPincode())
                                    .user(user)
                                    .build();
                            user.getAddresses().add(newAddress);
                        }
                    });
        return Optional.of(userRepository.save(user));
    }


    public User deleteAddress(Long id, Long addressId) {
        User user = userRepository.findById(id).orElseThrow();
        user.getAddresses()
                .stream()
                .filter(address -> address.getId().equals(addressId))
                .findFirst()
                .ifPresent(address -> user.getAddresses().remove(address));
        return userRepository.save(user);
    }

    @Override
    public boolean updatePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id).orElseThrow();
        if (passwordEncoder.matches(oldPassword, user.getPassword()))
            user.setPassword(passwordEncoder.encode(newPassword));
        else throw new AppException("Password does not match");
        userRepository.save(user);
        return true;
    }

    public User saveAvatar(Long id, MultipartFile file) throws IOException {
        User user = userRepository.findById(id).orElseThrow();
        if (user.getAvatar() != null)
            imageService.update(user.getAvatar().getId(), file, AVATAR_FOLDER);
        else {
            Image image = imageService.create(file, AVATAR_FOLDER);
            user.setAvatar(image);
        }
        return userRepository.save(user);
    }

    public void deleteAvatar(Long id) throws IOException {
        User user = userRepository.findById(id).orElseThrow();
        if (user.getAvatar() != null)
            imageService.delete(user.getAvatar().getId());
        else throw new AppException("Avatar does not exist");
        user.setAvatar(null);
        userRepository.save(user);
    }

    public Page<SharedContactDetailsDTO> getMySharedContactDetails(User user, Pageable pageable) {
        var details = user.getSharedContactDetails();
        return PageRes.from(details,pageable,SharedContactDetailsDTO::from);
    }

    public Page<SharedContactDetailsDTO> getSentSharedContactDetailRequests(User user, Pageable pageable) {
        var details = user.getRequestedSharedContactDetails();
        return PageRes.from(details,pageable,SharedContactDetailsDTO::from);
    }

    public void createShareContactDetailsRequest(User to, Long fromID) {
        User from = this.getUserById(fromID).orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, "user not found"));
        SharedContactDetails sharedContactDetails = SharedContactDetails.builder()
                .from(from)
                .to(to)
                .status(SharedContactDetails.Status.PENDING)
                .build();
        to.getRequestedSharedContactDetails().add(sharedContactDetails);
        saveUser(to);
    }

    public List<SharedContactDetails> updateSharedContactDetails(User from , Long toID, SharedContactDetails.Status status,String contactDetails){
        if(contactDetails!=null&&contactDetails!=""){
            status = SharedContactDetails.Status.ACCEPTED;
        }
       SharedContactDetails sharedContactDetails = from.getSharedContactDetails()
                .stream()
                .filter(details->details.getTo().getId().equals(toID))
                .findFirst()
                .orElseThrow(()->new APIException(HttpStatus.BAD_REQUEST,"User not found"));
        sharedContactDetails.setStatus(status);
        sharedContactDetails.setDetails(contactDetails);
        return this.saveUser(from).getSharedContactDetails();
    }
    public List<SharedContactDetails> deniedSharedContactDetailsRequest(User from , Long toID){
        return this.updateSharedContactDetails(from,toID, SharedContactDetails.Status.DENIED,null);
    }
    public void deleteSharedContactDetailsRequest(User to,Long fromID){
        SharedContactDetails sharedContactDetails = to.getRequestedSharedContactDetails()
                .stream()
                .filter(contactDetails->contactDetails.getFrom().getId().equals(fromID))
                .findFirst()
                .orElseThrow(()->new APIException(HttpStatus.BAD_REQUEST,"User not found"));
        to.getRequestedSharedContactDetails().remove(sharedContactDetails);
        saveUser(to);
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }


}
