package com.shelfconnect.controller;

import com.shelfconnect.Exception.APIException;
import com.shelfconnect.dto.api.APIResponse;
import com.shelfconnect.dto.api.Status;
import com.shelfconnect.dto.req.UpdatePasswordReq;
import com.shelfconnect.dto.req.UpdateProfileReq;
import com.shelfconnect.dto.res.AllBooksRes;
import com.shelfconnect.dto.res.FullProfileRes;
import com.shelfconnect.dto.res.UserInfoRes;
import com.shelfconnect.model.Book;
import com.shelfconnect.model.User;
import com.shelfconnect.security.user.UserDetails;
import com.shelfconnect.service.impl.BookService;
import com.shelfconnect.service.impl.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/*
 *  -- PROFILE RELATED --
 * 1. update profile
 * 2. update password
 * 3. view my books
 * 4. view my orders
 * 5. view my selling orders
 * 6. history
 * */
@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {
    private final UserService userService;
    private final BookService bookService;

    @Autowired
    public UserController(UserService userService, BookService bookService) {
        this.userService = userService;
        this.bookService = bookService;
    }

    @RequestMapping("/")
    @ResponseBody
    public ResponseEntity<APIResponse> greetMe(@AuthenticationPrincipal UserDetails authUser) {
        System.out.println(authUser);
        return ResponseEntity.ok(
                APIResponse.builder()
                        .statusCode(HttpStatus.OK)
                        .status(Status.SUCCESS)
                        .message("Welcome " + authUser.getUser().getName())
                        .data(Map.of("user", UserInfoRes.fromUser(authUser.getUser())))
                        .build()
        );
    }

    @GetMapping("/me")
    @ResponseBody
    public ResponseEntity<APIResponse> getMe(@AuthenticationPrincipal UserDetails authUser) {
        User user = userService.getUserById(authUser.getUser().getId()).orElseThrow();
        return ResponseEntity.ok(
                APIResponse.builder()
                        .statusCode(HttpStatus.OK)
                        .status(Status.SUCCESS)
                        .message("Welcome " + authUser.getUser().getName())
                        .data(Map.of("user", FullProfileRes.from(user)))
                        .build()
        );
    }

    @PutMapping("/me")
    @ResponseBody
    public ResponseEntity<APIResponse> putMe(
            @AuthenticationPrincipal UserDetails authUser,
            @Valid @RequestBody UpdateProfileReq updateProfileReq
    ) {
        System.out.println(updateProfileReq);
        User user = userService
                .updateUser(authUser.getUser().getId(), updateProfileReq)
                .orElseThrow(() -> APIException.forInternalServerError("Unable to update user"));
        return ResponseEntity.ok(
                APIResponse.builder()
                        .statusCode(HttpStatus.OK)
                        .status(Status.SUCCESS)
                        .message("profile updated successfully")
                        .data(Map.of("user", FullProfileRes.from(user)))
                        .build()
        );
    }


    @PutMapping(value = "/avatar", consumes = "multipart/form-data")
    public ResponseEntity<APIResponse> saveAvatar(
            @AuthenticationPrincipal UserDetails authUser,
            MultipartFile avatarFile
    ) {
        try {
            User user = userService.saveAvatar(authUser.getUser().getId(), avatarFile);
            return ResponseEntity.ok(
                    APIResponse.builder()
                            .statusCode(HttpStatus.OK)
                            .status(Status.SUCCESS)
                            .message("avatar set successfully")
                            .data(Map.of("user", UserInfoRes.fromUser(user)))
                            .build()
            );
        } catch (IOException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "unable to upload avatar");
        }
    }

    @DeleteMapping("/avatar")
    public ResponseEntity<APIResponse> deleteAvatar(
            @AuthenticationPrincipal UserDetails authUser
    ) {
        try {
            userService.deleteAvatar(authUser.getUser().getId());
            return ResponseEntity.ok(
                    APIResponse.builder()
                            .statusCode(HttpStatus.OK)
                            .status(Status.SUCCESS)
                            .message("avatar deleted successfully")
                            .build()
            );
        } catch (IOException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "unable to delete avatar");
        }
    }

    @PatchMapping("/update-password")
    @ResponseBody
    public ResponseEntity<APIResponse> updatePassword(
            @AuthenticationPrincipal UserDetails authUser,
            @Valid @NotNull @RequestBody UpdatePasswordReq updatePasswordReq
    ) {
        System.out.println(updatePasswordReq);
        boolean isUpdated = userService.updatePassword(
                authUser.getUser().getId(),
                updatePasswordReq.getOldPassword(),
                updatePasswordReq.getNewPassword()
        );
        if (isUpdated)
            return ResponseEntity.ok(
                    APIResponse.builder()
                            .statusCode(HttpStatus.OK)
                            .status(Status.SUCCESS)
                            .message("password updated successfully")
                            .build()
            );
        throw APIException.forInternalServerError("unable to update password");
    }

    @DeleteMapping("/address/{addressId}")
    @ResponseBody
    public ResponseEntity<APIResponse> deleteAddress(
            @AuthenticationPrincipal UserDetails authUser,
            @PathVariable Long addressId
    ) {
        userService.deleteAddress(authUser.getUser().getId(), addressId);
        return ResponseEntity.ok().body(APIResponse.builder()
                .statusCode(HttpStatus.OK)
                .status(Status.SUCCESS)
                .message("address deleted successfully")
                .build()
        );
    }

    @GetMapping("/my-books")
    @ResponseBody
    public ResponseEntity<APIResponse> myBooks(
            @AuthenticationPrincipal UserDetails authUser
    ) {
        Book bookExample = Book.builder().owner(authUser.getUser()).build();
        Page<Book> books = bookService
                .getAllBooks(Pageable.ofSize(10), Example.of(bookExample));
        return ResponseEntity.ok().body(
                APIResponse.builder()
                        .statusCode(HttpStatus.OK)
                        .status(Status.SUCCESS)
                        .data(AllBooksRes.from(books))
                        .build()
        );
    }

//    @PutMapping("/address")
//    @ResponseBody
//    public ResponseEntity<APIResponse> saveAddresses(
//            @AuthenticationPrincipal UserDetails userDetails,
//            @RequestBody List<AddressDTO> addressDTOS
//    ) {
//        User user = userService.saveAddresses(userDetails.getUser().getId(), addressDTOS);
//        return ResponseEntity.ok().body(
//                APIResponse.builder()
//                        .statusCode(HttpStatus.OK)
//                        .status(Status.SUCCESS)
//                        .message("avatar set successfully")
//                        .data(Map.of("user", FullProfileRes.from(user)))
//                        .build()
//        );
//    }


}
