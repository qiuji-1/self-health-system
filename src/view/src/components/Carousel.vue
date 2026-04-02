<template>
    <div class="hero-carousel-container">
        <div v-if="carouselItems.length === 0">
            暂无轮播图数据
        </div>
        <div v-else>
            <div class="title">
                {{ carouselItems[currentCarouselIndex].title }}
            </div>
            <div class="summary">
                {{ carouselItems[currentCarouselIndex].subtitle }}
                <span @click="handleDetail(carouselItems[currentCarouselIndex])"><i
                        class="el-icon-document-copy"></i>查看详情</span>
            </div>
            <div class="cover-point">
                <div class="item-point">
                    <img :style="{ border: currentCarouselIndex === index ? '2px solid rgb(23, 197, 116)' : '' }"
                        @click="changeCarousel(index)" v-for="(item, index) in carouselItems" :key="index"
                        :src="item.image" :alt="item.title" srcset="">
                </div>
            </div>
        </div>

    </div>
</template>

<script>
export default {
    name: "HeroCarousel",
    props: {
        showBtn: {
            type: Boolean,
            default: true
        },
        containerHeight: {
            type: String,
            default: 'auto'
        },
        carouselItems: {
            type: Array,
            default: () => [],
            validator: value => value.every(item => item.image && item.title && item.subtitle)
        },
        interval: {
            type: Number,
            default: 5000
        }
    },
    data() {
        return {
            currentCarouselIndex: 0,
            carouselInterval: null
        }
    },
    computed: {
        currentItem() {
            return this.carouselItems[this.currentCarouselIndex] || {}
        }
    },
    mounted() {
        this.startCarousel()
    },
    beforeDestroy() {
        clearInterval(this.carouselInterval)
    },
    methods: {
        handleDetail(obj) {
            this.$emit('obj-detail', obj)
        },
        changeCarousel(index) {
            this.currentCarouselIndex = index
            this.resetCarousel()
        },
        startCarousel() {
            if (this.carouselItems.length <= 1) return

            this.carouselInterval = setInterval(() => {
                this.currentCarouselIndex =
                    (this.currentCarouselIndex + 1) % this.carouselItems.length
            }, this.interval)
        },
        resetCarousel() {
            clearInterval(this.carouselInterval)
            this.startCarousel()
        }
    }
}
</script>

<style scoped lang="scss">
.hero-carousel-container {
    font-size: 12px;
    padding: 30px;
    border-radius: 15px;
    background-color: rgb(36, 136, 17);
    // box-shadow: 0 1px 2px rgb(36, 136, 17);

    .title {
        font-size: 26px;
        color: rgb(255, 255, 255);
        font-weight: 600;
        min-height: 50px;
    }

    .summary {
        font-size: 16px;
        margin-block: 10px;
        line-height: 30px;
        color: rgb(255, 255, 255);
        min-height: 70px;

        span {
            display: inline-block;
            background-color: rgb(245, 245, 46);
            padding: 4px 10px;
            border-radius: 20px;
            cursor: pointer;
            color: rgb(51, 51, 51);
            font-size: 14px;

            &:hover {
                background-color: rgb(237, 237, 12);
            }
        }
    }

    .cover-point {
        display: flex;
        justify-content: flex-end;

        .item-point {
            display: flex;
            background-color: rgba(51, 51, 51);
            padding: 6px;
            border-radius: 2px;

            img {
                width: 60px;
                height: 40px;
                border: 2px solid rgb(51, 51, 51);
                cursor: pointer;

                &:hover {
                    border: 2px solid rgb(23, 197, 116);
                }
            }

        }
    }
}
</style>