package com.shelfconnect.dto.res;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;

@Data
@Builder
public class PageRes {

  public static <T,R> Page<R> from(List<T> content, Pageable pageable, Function<? super T,? extends R> mapper){
      return new PageImpl<>(content,pageable,content.size()).map(mapper);
  }
}
