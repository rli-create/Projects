package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NoteMapper {
    @Select("SELECT * FROM Notes WHERE userid = #{userid}")
    List<Note> getUserNotes(Integer userid);

    //@Insert("INSERT INTO Notes (notetitle, notedescription, userid) VALUES(#{notetitle}, #{notedescription}, #{userid})")
    /*@Insert("INSERT INTO Notes (notetitle, notedescription, userid)\n" +
            "VALUES(#{notetitle}, #{notedescription}, #{userid}) ON\n" +
            "DUPLICATE KEY UPDATE\n" +
            "notetitle = VALUES('notetitle'), notedescription = VALUES('notedescription')")*/
    @Insert("INSERT INTO Notes (notetitle, notedescription, userid) VALUES(#{notetitle}, #{notedescription}, #{userid})")
    @Options(useGeneratedKeys = true, keyProperty = "noteid")
    Integer insert(Note note);

    @Insert("UPDATE Notes SET notetitle = #{notetitle}, notedescription = #{notedescription} WHERE noteid = #{noteid}")
    Integer update(Note note);

    @Delete("DELETE FROM Notes WHERE noteid = #{noteid}")
    Integer delete(Integer noteid);
}
