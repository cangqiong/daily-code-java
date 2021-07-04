package com.chason.base.learning.lms.store.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Position {

    private long start;
    private long len;
}
