export interface Schedule {
  startDate: string;
  endDate: string;
  durationMinutes: number;
  recurrence: {
    daysOfWeek: string[];
    startTime: string;
    endTime: string;
  };
}
