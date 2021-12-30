package com.example.tileswipe;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PuzzleGameDao {
    @Query("DELETE FROM PuzzleGame")
    void clearTable();

    @Query("SELECT * FROM PuzzleGame")
    List<PuzzleGame> getAll();

    @Query("SELECT * FROM PuzzleGame WHERE uid IN (:userIds)")
    List<PuzzleGame> getGamesWithIds(int[] userIds);

    @Query("SELECT * FROM PuzzleGame ORDER BY ms_elapsed ASC")
    List<PuzzleGame> getFastestGames();

    @Query("SELECT EXISTS(SELECT * FROM PuzzleGame WHERE uid = (:id))")
    boolean checkIfGameExists(int id);

    @Query("SELECT * FROM PuzzleGame ORDER BY move_count ASC")
    List<PuzzleGame> getLowestMoveCountGames();

    @Query("SELECT * FROM PuzzleGame ORDER BY last_updated DESC")
    List<PuzzleGame> getMostRecentGames();

    @Insert
    void insertGames(PuzzleGame... puzzleGames);

    @Delete
    void delete(PuzzleGame puzzleGame);

    @Update
    public void updateGames(PuzzleGame... puzzleGames);
}
