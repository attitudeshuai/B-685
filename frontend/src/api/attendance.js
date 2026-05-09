import request from './request'

export const attendanceApi = {
  clockIn: (userId) => request.post('/attendance/clock-in', null, { params: { userId } }),
  clockOut: (userId) => request.post('/attendance/clock-out', null, { params: { userId } }),
  getTodayRecord: (userId) => request.get('/attendance/today', { params: { userId } }),
  getMonthlyRecords: (userId, year, month) => request.get('/attendance/monthly', { params: { userId, year, month } }),
  getRecords: (params) => request.get('/attendance/records', { params }),
  getMonthlySummary: (params) => request.get('/attendance/summary', { params })
}
