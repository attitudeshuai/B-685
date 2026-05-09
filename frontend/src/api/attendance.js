import request from './request'

export const attendanceApi = {
  checkIn: (userId) => request.post('/attendance/check-in', null, { params: { userId } }),
  checkOut: (userId) => request.post('/attendance/check-out', null, { params: { userId } }),
  getTodayRecord: (userId) => request.get('/attendance/today', { params: { userId } }),
  getMyRecords: (userId, startDate, endDate) => request.get('/attendance/my-records', { params: { userId, startDate, endDate } }),
  getAllRecords: (params) => request.get('/attendance/records', { params }),
  getMonthlySummary: (year, month) => request.get('/attendance/monthly-summary', { params: { year, month } })
}
