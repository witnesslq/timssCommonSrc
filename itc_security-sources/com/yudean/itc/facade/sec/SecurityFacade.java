package com.yudean.itc.facade.sec;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yudean.itc.dao.sec.SecureUserMapper;
import com.yudean.itc.dto.sec.SecureUser;

@Service
public class SecurityFacade implements ISecurityFacade {
    private static final Logger log = Logger.getLogger( SecurityFacade.class );

    @Autowired
    private SecureUserMapper secureUserMapper;

    @Override
    public List<SecureUser> retriveActiveUsersWithSpecificRole(String roleId, String orgCode,
            String organizationQueryType) {
        List<SecureUser> userList = secureUserMapper.selectActiveUsersInOrgsWithRoleOrGroup( roleId, null, orgCode,
                organizationQueryType );
        return userList;
    }

    @Override
    public List<SecureUser> retriveActiveUsersWithSpecificGroup(String groupId, String orgCode,
            String organizationQueryType) {
        List<SecureUser> userList = secureUserMapper.selectActiveUsersInOrgsWithRoleOrGroup( null, groupId, orgCode,
                organizationQueryType );
        return userList;
    }
}
