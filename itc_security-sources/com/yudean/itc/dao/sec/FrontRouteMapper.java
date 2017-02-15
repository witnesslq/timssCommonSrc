package com.yudean.itc.dao.sec;

import com.yudean.itc.dto.sec.FavRoute;
import com.yudean.itc.dto.sec.FrontRoute;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by 890157 on 2016/10/12.
 */
public interface FrontRouteMapper {
    List<FrontRoute> selectAllRoute(@Param("site") String site);

    List<FavRoute> selectFavRoute(@Param("userId")String userId, @Param("site")String site);

    void deleteFavRoute(@Param("userId")String userId, @Param("ids")String[] ids);

    void insertFavRoute(@Param("userId")String userId, @Param("ids")String[] ids, @Param("siteId") String siteId);

    Integer selectIsRouteExist(@Param("userId")String userId, @Param("routeId")String routeId, @Param("siteId") String siteId);
}
