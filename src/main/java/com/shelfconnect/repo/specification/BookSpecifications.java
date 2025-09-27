package com.shelfconnect.repo.specification;

import com.shelfconnect.model.Address;
import com.shelfconnect.model.Book;
import com.shelfconnect.model.Category;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
@Component
public class BookSpecifications {
    public Specification<Book> title(String title) {
        return (root, cq, cb) -> {
            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    public Specification<Book> author(String author) {
        return (root, cq, cb) -> {
            return cb.equal(root.get("author"), author);
        };
    }

    public Specification<Book> isbn(String isbn) {
        return (root, query, cb) -> {
            return cb.equal(root.get("isbn"), isbn);
        };
    }

    public Specification<Book> categories(List<Long> categories) {
        return (root, cq, cb) -> {
            cq.distinct(true);
            Join<Book, Category> join = root.join("categories", JoinType.INNER);
            return join.get("id").in(categories);
        };
    }

    public Specification<Book> maxPrice(BigDecimal maxPrice) {
        return (root, cq, cb) -> {
            return cb.le(root.get("sellingPrice"), maxPrice);
        };
    }

    public Specification<Book> condition(String condition) {
        return (root, cq, cb) -> {
            return cb.equal(root.get("condition"), condition);
        };
    }

    public Specification<Book> inStock(boolean inStock) {
        return (root, cq, cb) -> {
            if (inStock)
                return cb.greaterThan(root.get("quantity"), 0);

            return null;
        };
    }

    public Specification<Book> nearBy(List<Integer> nearBy) {
        int lat = nearBy.get(0);
        int lon = nearBy.get(1);
        int radius = nearBy.get(2);
        return (root, cq, cb) -> {
            cq.distinct(true);
            Join<Book, Address> bookAddressJoin = root.join("addresses", JoinType.INNER);
            Path<Point> location = bookAddressJoin.get("location");
            // Build WKT for the center point with order: lon lat
            String wkt = String.format("POINT(%s %s)", lon, lat);

            // ST_GeomFromText('POINT(lon lat)', 4326)
            Expression<Object> centerGeom = cb.function(
                    "ST_GeomFromText",
                    Object.class,
                    cb.literal(wkt),
                    cb.literal(4326)
            );
            Expression<Double> distance = cb.function("ST_Distance_Sphere", Double.class, location, centerGeom);
            return cb.lessThanOrEqualTo(distance, (double) radius);
        };
    }


}







































