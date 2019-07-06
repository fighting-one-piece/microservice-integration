package org.platform.modules.user.service.converter;

import org.platform.modules.abstr.service.converter.ConverterAbstrImpl;
import org.platform.modules.abstr.service.converter.IConverter;
import org.platform.modules.abstr.utils.EntityAttributeUtils;
import org.platform.modules.user.entity.User;
import org.platform.modules.user.entity.UserPO;

public class UserConverter extends ConverterAbstrImpl<User, UserPO> {
	
	public static final class UserConverterHolder {
		private static UserConverter INSTANCE = new UserConverter();
	}

	public static IConverter<?, ?> getInstance() {
		return UserConverterHolder.INSTANCE;
	}
	
	@Override
	public void convertEntity2DTO(User user, UserPO userPO) {
		super.convertEntity2DTO(user, userPO);
		EntityAttributeUtils.fillEntity(user.getAttributes(), userPO);
	}
	
}
