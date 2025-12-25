<template>
  <div class="heatmap-calendar">
    <div class="calendar-header">
      <h3 class="calendar-title">{{ title }}</h3>
      <div class="calendar-nav">
        <el-button size="small" @click="prevYear">
          <el-icon>
            <ArrowLeft />
          </el-icon>
        </el-button>
        <span class="current-year">{{ currentYear }}</span>
        <el-button size="small" @click="nextYear">
          <el-icon>
            <ArrowRight />
          </el-icon>
        </el-button>
      </div>
    </div>

    <div class="calendar-body">
      <!-- 主容器：星期标签 + 月份和日历内容 -->
      <div class="calendar-content">
        <!-- 星期标签（左侧纵向） -->
        <div class="weekday-labels">
          <div v-for="day in displayWeekdays" :key="day" class="weekday-label">
            {{ day }}
          </div>
        </div>

        <!-- 右侧内容：月份标题 + 日历网格 -->
        <div class="calendar-main">
          <!-- 月份标题行 -->
          <div class="month-labels">
            <div v-for="month in months" :key="month" class="month-label">
              {{ month }}
            </div>
          </div>

          <!-- 日历网格 -->
          <div class="calendar-grid">
            <div v-for="(day, index) in calendarData" :key="index" :class="[
              'calendar-cell',
              { 'has-data': day.hasData },
              { 'current-date': isCurrentDate(day.date) },
              { 'other-month': day.isOtherMonth },
            ]" :style="{ gridRow: day.weekday + 1 }" @mouseover="handleMouseOver(day, $event)"
              @mouseleave="handleMouseLeave" :data-date="day.fullDate" :data-hasdata="day.hasData" />
          </div>

          <!-- 悬停提示 - 移到calendar-grid外部 -->
          <div v-if="tooltip.visible" class="tooltip" :style="{ left: tooltip.x + 'px', top: tooltip.y + 'px' }">
            {{ tooltip.date }}
            <span v-if="tooltip.hasData" class="tooltip-status has-data">已写日记</span>
            <span v-else class="tooltip-status no-data">未写日记</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, watch } from "vue";
import { ArrowLeft, ArrowRight } from "@element-plus/icons-vue";

interface Props {
  year?: number;
  title?: string;
  hasDataDates?: string[];
}

const props = withDefaults(defineProps<Props>(), {
  year: () => new Date().getFullYear(),
  title: "日记记录热力图",
  hasDataDates: () => [],
});

const emit = defineEmits<{
  (e: "update:year", year: number): void;
  (e: "date-click", date: string): void;
}>();

// 响应式数据
const currentYear = ref(props.year);
const months = [
  "1月",
  "2月",
  "3月",
  "4月",
  "5月",
  "6月",
  "7月",
  "8月",
  "9月",
  "10月",
  "11月",
  "12月",
];
const weekdays = ["", "周日", "周一", "周二", "周三", "周四", "周五", "周六"];
const displayWeekdays = computed(() => weekdays);
const calendarData = ref<CalendarDay[]>([]);

// 悬停提示数据
const tooltip = ref({
  visible: false,
  x: 0,
  y: 0,
  date: "",
  hasData: false,
});

interface CalendarDay {
  date: Date;
  fullDate: string; // YYYY-MM-DD格式
  day: number;
  month: number;
  year: number;
  hasData: boolean;
  isOtherMonth: boolean;
  weekday: number; // 0-6，0表示周日
}

// 计算当前日期
const isCurrentDate = (date: Date) => {
  const today = new Date();
  return (
    date.getFullYear() === today.getFullYear() &&
    date.getMonth() === today.getMonth() &&
    date.getDate() === today.getDate()
  );
};

const generateCalendarData = () => {
  const data: CalendarDay[] = [];
  const hasDataSet = new Set(props.hasDataDates);

  const startDate = new Date(currentYear.value, 0, 1);
  const firstDayOfYear = startDate.getDay();
  const prevYearDays = firstDayOfYear;

  // weeklyData[0] = 周日, weeklyData[1] = 周一, ..., weeklyData[6] = 周六
  const weeklyData: CalendarDay[][] = Array.from({ length: 7 }, () => []);

  // 辅助函数：确保weeklyData[weekday]存在并返回它
  const getWeeklyDayArray = (weekday: number): CalendarDay[] => {
    if (weekday >= 0 && weekday < 7 && weeklyData[weekday]) {
      return weeklyData[weekday];
    }
    return [];
  };

  // 添加前一年的日期
  for (let i = prevYearDays - 1; i >= 0; i--) {
    const date = new Date(currentYear.value, 0, -i);
    const weekday = date.getDay();
    getWeeklyDayArray(weekday).push({
      date,
      fullDate: formatDate(date),
      day: date.getDate(),
      month: date.getMonth(),
      year: date.getFullYear(),
      hasData: hasDataSet.has(formatDate(date)),
      isOtherMonth: true,
      weekday,
    });
  }

  // 添加当前年份的所有日期
  for (let month = 0; month < 12; month++) {
    const daysInMonth = new Date(currentYear.value, month + 1, 0).getDate();
    for (let day = 1; day <= daysInMonth; day++) {
      const date = new Date(currentYear.value, month, day);
      const weekday = date.getDay();
      getWeeklyDayArray(weekday).push({
        date,
        fullDate: formatDate(date),
        day,
        month,
        year: currentYear.value,
        hasData: hasDataSet.has(formatDate(date)),
        isOtherMonth: false,
        weekday,
      });
    }
  }

  // 添加下一年的日期，补齐最后一列
  const endDate = new Date(currentYear.value, 11, 31);
  const lastDayOfYear = endDate.getDay();
  const nextYearDays = 6 - lastDayOfYear;

  for (let i = 1; i <= nextYearDays; i++) {
    const date = new Date(currentYear.value + 1, 0, i);
    const weekday = date.getDay();
    getWeeklyDayArray(weekday).push({
      date,
      fullDate: formatDate(date),
      day: date.getDate(),
      month: date.getMonth(),
      year: date.getFullYear(),
      hasData: hasDataSet.has(formatDate(date)),
      isOtherMonth: true,
      weekday,
    });
  }

  // 找出最长的一列（最多周数）
  const maxWeeks = Math.max(...weeklyData.map((week) => week.length));

  // 按CSS Grid行顺序排列数据
  for (let week = 0; week < maxWeeks; week++) {
    for (let weekday = 0; weekday < 7; weekday++) {
      // 安全地获取每周的数据数组和具体日期数据
      const weekArray = getWeeklyDayArray(weekday);
      const dayData = weekArray[week];
      if (dayData) {
        data.push(dayData);
      }
    }
  }

  calendarData.value = data;
};

const formatDate = (date: Date): string => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

const formatFullDate = (date: Date): string => {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  return `${year}年${month}月${day}日`;
};

const prevYear = () => {
  currentYear.value--;
  emit("update:year", currentYear.value);
  generateCalendarData();
};

const nextYear = () => {
  currentYear.value++;
  emit("update:year", currentYear.value);
  generateCalendarData();
};

const handleMouseOver = (day: CalendarDay, event: MouseEvent) => {
  tooltip.value.visible = true;
  tooltip.value.date = formatFullDate(day.date);
  tooltip.value.hasData = day.hasData;

  if (event && event.currentTarget) {
    const rect = (event.currentTarget as HTMLElement).getBoundingClientRect();
    tooltip.value.x = rect.left + window.scrollX + rect.width / 2;
    tooltip.value.y = rect.top + window.scrollY - (30 + rect.height);
  }
};

const handleMouseLeave = () => {
  tooltip.value.visible = false;
};

// 监听变化
watch(() => currentYear.value, generateCalendarData);
watch(() => props.hasDataDates, generateCalendarData, { deep: true });

onMounted(generateCalendarData);
</script>

<style scoped>
.heatmap-calendar {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  padding: 20px;
  margin-bottom: 20px;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.calendar-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.calendar-nav {
  display: flex;
  align-items: center;
  gap: 12px;
}

.current-year {
  font-size: 16px;
  font-weight: 500;
  color: #666;
  min-width: 60px;
  text-align: center;
}

.calendar-body {
  overflow-x: auto;
  padding-bottom: 10px;
}

.calendar-content {
  display: flex;
  align-items: flex-start;
}

.weekday-labels {
  display: grid;
  grid-template-rows: repeat(7, minmax(2px, 0.9fr));
  gap: 0px;
  margin-right: 8px;
  width: 24px;
  align-items: center;
  padding-top: 2px;
}

.weekday-label {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  color: #999;
  padding: 0;
  aspect-ratio: 1;
  min-height: 2px;
}

.calendar-main {
  flex: 1;
}

.month-labels {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 0.5px;
  margin-bottom: 8px;
}

.month-label {
  text-align: center;
  font-size: 12px;
  font-weight: 500;
  color: #666;
  padding: 4px 0;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(53, minmax(3px, 1fr));
  grid-template-rows: repeat(7, minmax(2px, 1fr));
  grid-auto-flow: column;
  gap: 1px;
  position: relative;
}

.calendar-cell {
  aspect-ratio: 1;
  background-color: #f5f5f5;
  border-radius: 0.5px;
  min-height: 2px;
  transition: all 0.2s ease;
  position: relative;
  cursor: pointer;
  z-index: 1;
}

.calendar-cell.has-data {
  background-color: #4ade80;
}

.calendar-cell.current-date {
  border: 2px solid #0ea5e9;
}

.calendar-cell.other-month {
  opacity: 0.3;
}

.calendar-cell:hover {
  transform: scale(1.1);
  z-index: 1;
}

.calendar-cell.has-data:hover {
  background-color: #22c55e;
}

.tooltip {
  position: fixed;
  background-color: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 6px 8px;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
  z-index: 1000;
  transform: translateX(-50%);
  pointer-events: none;
  display: block !important;
}

.tooltip::after {
  content: "";
  position: absolute;
  bottom: -5px;
  left: 50%;
  transform: translateX(-50%);
  border-width: 5px 5px 0;
  border-style: solid;
  border-color: rgba(0, 0, 0, 0.8) transparent transparent;
}

.tooltip-status {
  display: block;
  font-size: 11px;
  margin-top: 2px;
  opacity: 0.9;
}

.tooltip-status.has-data {
  color: #4ade80;
}

.tooltip-status.no-data {
  color: #999;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .calendar-grid {
    grid-template-columns: repeat(53, minmax(2.5px, 1fr));
    gap: 0.5px;
  }

  .weekday-labels {
    width: 14px;
  }

  .weekday-label {
    font-size: 8px;
    width: 14px;
    height: 4px;
  }
}

@media (max-width: 992px) {
  .calendar-grid {
    grid-template-columns: repeat(53, minmax(2px, 1fr));
    gap: 0.5px;
  }

  .weekday-label {
    font-size: 7px;
    width: 12px;
    height: 3px;
  }

  .month-label {
    font-size: 10px;
  }
}

@media (max-width: 768px) {
  .heatmap-calendar {
    padding: 16px;
  }

  .calendar-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .calendar-nav {
    justify-content: center;
  }

  .calendar-grid {
    grid-template-columns: repeat(53, minmax(1.5px, 1fr));
    gap: 0.5px;
  }

  .weekday-labels {
    width: 10px;
  }

  .weekday-label {
    font-size: 7px;
    height: 3px;
  }

  .month-labels {
    display: none;
  }
}

@media (max-width: 480px) {
  .calendar-grid {
    grid-template-columns: repeat(53, minmax(1px, 1fr));
    gap: 0.5px;
  }

  .weekday-labels {
    display: none;
  }
}
</style>