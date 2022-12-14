package ru.practicum.paging;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.exception.BadRequestException;

public class OffsetLimitPageable extends PageRequest {

    private static final int MAX_PAGE_SIZE = 256;

    private final int offset;
    private final int size;
    private final Sort sort;

    private OffsetLimitPageable(int offset, int size, Sort sort) {
        super(0, size, sort);
        this.offset = offset;
        this.size = size;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return 0;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public PageRequest next() {
        return new OffsetLimitPageable(offset + size, size, sort);
    }

    @Override
    public Pageable previousOrFirst() {
        if ((offset - size) <= 0)
            return new OffsetLimitPageable(offset, size, sort);
        else return new OffsetLimitPageable(offset - size, size, sort);
    }

    @Override
    public PageRequest first() {
        return new OffsetLimitPageable(offset, size, sort);
    }

    @Override
    public PageRequest withPage(int pageNumber) {
        return new OffsetLimitPageable(offset + size * pageNumber, size, sort);
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }

    public static Pageable create(Integer offset, Integer size, Sort sort) {
        if (offset == null && size == null) {
            return new OffsetLimitPageable(0, MAX_PAGE_SIZE, sort);
        }
        validatePaging(offset, size);
        return new OffsetLimitPageable(offset, size, sort);
    }

    private static void validatePaging(Integer from, Integer size) {
        if (from == null && size == null) return;
        if (from == null || size == null)
            throw new BadRequestException("Must provide both from and size or no one", "Size or from is absent");
        if (size <= 0) throw new BadRequestException("Size must be positive", "Size " + size + " <=0");
        if (from < 0) throw new BadRequestException("From must be positive or 0", "From " + from + " <0");
    }
}
