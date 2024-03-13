package com.gentle.store.customer.dto;

import com.gentle.store.customer.entity.CustomerActivity;
import com.gentle.store.customer.entity.enums.ActivityType;

/**
 * DTO for {@link CustomerActivity}
 */
public record CustomerActivityDTO(
        ActivityType activityType,
        String content
) {
}