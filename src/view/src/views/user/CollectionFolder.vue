<template>
  <div class="collection-container">
    <!-- 收藏资讯信息 -->
    <div>
      <CollectionHealthNews/>
    </div>
    <!-- 收藏的食谱信息 -->
     <div>
      <CollectionRecipe/>
     </div>
  </div>
</template>

<script>
import CollectionHealthNews from "@/views/user/CollectionHealthNews.vue";
import CollectionRecipe from "@/views/user/CollectionRecipe.vue";
export default {
  components: { CollectionHealthNews,CollectionRecipe },
  data() {
    return {

    };
  },

  methods: {
 
  },
};
</script>

<style scoped lang="scss">

</style>