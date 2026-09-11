sed -i '/@Query("SELECT \* FROM posts ORDER BY timestamp DESC")/d' app/src/main/java/com/example/data/LocaliDao.kt
sed -i '/fun getAllPosts()/i \    @Query("SELECT * FROM posts ORDER BY timestamp DESC")' app/src/main/java/com/example/data/LocaliDao.kt
