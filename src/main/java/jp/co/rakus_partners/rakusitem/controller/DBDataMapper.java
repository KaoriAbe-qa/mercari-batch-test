package jp.co.rakus_partners.rakusitem.controller;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import jp.co.rakus_partners.rakusitem.entity.Item;

@Mapper
public interface DBDataMapper {

	/**
     * ファイルデータテーブル(file_data)から全件取得する
     * @return ファイルデータテーブル(file_data)のデータリスト
     */
    @Select("SELECT id, name, condition, category, brand, price, shipping, description, "
            + " FROM items ORDER BY id asc")
    List<Item> findAll();
}
