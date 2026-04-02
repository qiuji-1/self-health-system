package com.kmbeast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kmbeast.pojo.dto.RecipeQueryDto;
import com.kmbeast.pojo.entity.Recipe;
import com.kmbeast.pojo.vo.RecipeListItemVO;
import com.kmbeast.pojo.vo.RecipeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 食谱持久化接口
 */
@Mapper
public interface RecipeMapper extends BaseMapper<Recipe> {

    List<RecipeListItemVO> list(RecipeQueryDto recipeQueryDto);

    Integer listPageCount(RecipeQueryDto recipeQueryDto);

    RecipeVO getRecipeVOById(@Param(value = "id") Integer id);

    List<Integer> listIds();


}
