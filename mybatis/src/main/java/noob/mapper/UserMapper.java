package noob.mapper;

import java.util.List;
import noob.entity.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User row);

    User selectByPrimaryKey(Integer id);

    List<User> selectAll();

    int updateByPrimaryKey(User row);
}