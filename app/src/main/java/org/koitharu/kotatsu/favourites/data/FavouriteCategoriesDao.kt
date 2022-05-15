package org.koitharu.kotatsu.favourites.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FavouriteCategoriesDao {

	@Query("SELECT * FROM favourite_categories WHERE category_id = :id AND deleted_at = 0")
	abstract suspend fun find(id: Int): FavouriteCategoryEntity

	@Query("SELECT * FROM favourite_categories WHERE deleted_at = 0 ORDER BY sort_key")
	abstract suspend fun findAll(): List<FavouriteCategoryEntity>

	@Query("SELECT * FROM favourite_categories WHERE deleted_at = 0 ORDER BY sort_key")
	abstract fun observeAll(): Flow<List<FavouriteCategoryEntity>>

	@Query("SELECT * FROM favourite_categories WHERE category_id = :id AND deleted_at = 0")
	abstract fun observe(id: Long): Flow<FavouriteCategoryEntity?>

	@Insert(onConflict = OnConflictStrategy.ABORT)
	abstract suspend fun insert(category: FavouriteCategoryEntity): Long

	@Update
	abstract suspend fun update(category: FavouriteCategoryEntity): Int

	@Query("UPDATE favourite_categories SET deleted_at = :now WHERE category_id = :id")
	abstract suspend fun delete(id: Long, now: Long = System.currentTimeMillis())

	@Query("UPDATE favourite_categories SET title = :title, `order` = :order, `track` = :tracker  WHERE category_id = :id")
	abstract suspend fun update(id: Long, title: String, order: String, tracker: Boolean)

	@Query("UPDATE favourite_categories SET `order` = :order WHERE category_id = :id")
	abstract suspend fun updateOrder(id: Long, order: String)

	@Query("UPDATE favourite_categories SET sort_key = :sortKey WHERE category_id = :id")
	abstract suspend fun updateSortKey(id: Long, sortKey: Int)

	@Query("DELETE FROM favourite_categories WHERE deleted_at != 0")
	abstract suspend fun gc()

	@Query("SELECT MAX(sort_key) FROM favourite_categories WHERE deleted_at = 0")
	protected abstract suspend fun getMaxSortKey(): Int?

	suspend fun getNextSortKey(): Int {
		return (getMaxSortKey() ?: 0) + 1
	}

	@Transaction
	open suspend fun upsert(entity: FavouriteCategoryEntity) {
		if (update(entity) == 0) {
			insert(entity)
		}
	}
}