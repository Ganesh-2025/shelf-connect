package com.shelfconnect.util;

import com.shelfconnect.Exception.APIException;
import com.shelfconnect.constant.AppConstants;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public class BookReqParamParser {
    private static final Pattern ISBN10OR13 = Pattern.compile("^(\\d{9}[\\dX]|\\d{13})$");
    private static final String[] sortableParams = {"title,condition,price"};

    public record ParamReq(String title, //not empty but can be null
                           String author,//not empty if present
                           String isbn,// must be in std isbn format
                           List<Long> categories,   // e.g. "5,6" convert to List<Long>
                           BigDecimal maxPrice, // must be double if present & convert to double
                           String condition,// must be from enum categories
                           boolean inStock,     // "true"/"false"
                           List<Integer> nearby,       // "lat,lng,radiusKm" convert to lat,lan,radius if present
                           Sort sort,           // "price,asc" craft proper pageable instance from page,size,sort
                           int page,
                           int size) {
    }

    public static ParamReq parse(
            String title, //not empty but can be null
            String author,//not empty if present
            String isbn,// must be in std isbn format
            String category,   // e.g. "5,6" convert to List<Long>
            String maxPrice, // must be double if present & convert to double
            String condition,// must be from enum categories
            String inStock,     // "true"/"false"
            String nearby,       // "lat,lng,radiusKm" convert to lat,lan,radius if present
            String sort,           // "price,asc" craft proper pageable instance from page,size,sort
            String page,
            String size
    ) {
        String parsedTitle = null;
        String parsedAuthor = null;
        String parsedIsbn = null;
        List<Long> parsedCategories = null;
        BigDecimal parsedMaxPrice = null;
        Boolean parsedInStock = null;
        List<Integer> parsedNearBy = null;
        Sort parsedSort = null;
        int parsedPage = 0;
        int parseSize = 10;
        String parsedCondition = null;
        try {
            if (title != null) {
                if (title.isBlank()) throw new Exception();
                parsedTitle = title.trim();
            }
            if (author != null) {
                if (author.isBlank()) throw new Exception();
                parsedAuthor = author.trim();
            }
            if (isbn != null && !ISBN10OR13.matcher(isbn).matches()) throw new Exception();
            else parsedIsbn = isbn;
            if (category != null) {
                parsedCategories = parseCategories(category.trim());
            }
            if (maxPrice != null) {
                parsedMaxPrice = parseMaxPrice(maxPrice.trim());
            }
            if (condition != null) {
                parsedCondition = parseCondition(condition.trim());
            }
            if (inStock != null) {
                parsedInStock = parseInStock(inStock.trim());
            }
            if (nearby != null) {
                parsedNearBy = parseNearBy(nearby.trim());
            }
            if (sort != null) {
                parsedSort = parseSort(sort.trim());
            }
            if (page != null) {
                parsedPage = Integer.parseInt(page);
            }
            if (size != null) {
                parseSize = Integer.parseInt(size.trim());
            }
            return new ParamReq(
                    parsedTitle,
                    parsedAuthor,
                    parsedIsbn,
                    parsedCategories,
                    parsedMaxPrice,
                    parsedCondition,
                    parsedInStock,
                    parsedNearBy,
                    parsedSort,
                    parsedPage,
                    parseSize
            );
        }catch (Exception ex){
            throw new APIException(HttpStatus.BAD_REQUEST,"unable to process request params");
        }

    }


    private static List<Long> parseCategories(String category) throws NumberFormatException {
        List<Long> categories = Arrays.stream(category.trim().split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();
        return categories;
    }

    private static BigDecimal parseMaxPrice(String maxPrice) throws NumberFormatException {
        return new BigDecimal(maxPrice.trim());
    }

    private static String parseCondition(String condition) throws NoSuchElementException {
        return Arrays.stream(AppConstants.bookConditions).filter(condition::equals).findFirst().orElseThrow();
    }

    private static boolean parseInStock(String inStock) {
        return switch (inStock) {
            case "true" -> true;
            case "false" -> false;
            default -> throw getExFor("inStock");
        };
    }

    private static List<Integer> parseNearBy(String nearBy) {
        List<Integer> locParams = Arrays.stream(nearBy.split(","))
                .map(Integer::parseInt)
                .toList();
        return locParams;
    }

    private static Sort parseSort(String sort) {
        String[] arr = sort.split(",");
        String field = Arrays.stream(sortableParams)
                .map(String::toLowerCase)
                .filter(arr[0]::equals)
                .findFirst()
                .orElseThrow(() -> getExFor("sort"));
        return switch (arr[1]) {
            case "asc" -> Sort.by(arr[0]).ascending();
            case "desc" -> Sort.by(arr[0]).descending();
            default -> throw getExFor("sort");
        };
    }

    private static APIException getExFor(@NotNull String field) {
        return new APIException(HttpStatus.BAD_REQUEST, "unable to parse field : " + field);
    }
}
