package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CredentialMapper {

    @Select("SELECT * FROM Credentials WHERE userid = #{userid}")
    List<Credential> getUserCredentials(Integer userid);

    @Select("SELECT * FROM Credentials WHERE credentialid = #{credentialid}")
    Credential get(Integer credentialid);

    @Insert("INSERT INTO Credentials (url, username, key, password, userid) VALUES (#{url}, #{username}, #{key}, #{password}, #{userid})")
    Integer insert(Credential credential);

    @Update("UPDATE Credentials SET url = #{url}, username = #{username}, password = #{password} WHERE credentialid = #{credentialid}")
    Integer update(Credential credential);

    @Delete("DELETE FROM Credentials WHERE credentialid = #{credentialid}")
    Integer delete(Integer credentialid);
}
