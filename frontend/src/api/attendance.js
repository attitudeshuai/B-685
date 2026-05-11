import request from './request'

export const attendanceApi = {
  checkIn: (userId, location, ip) => request.post('/attendance/check-in', { userId, location, ip }),

  checkOut: (userId, location, ip) => request.post('/attendance/check-out', { userId, location, ip }),

  getTodayAttendance: (userId) => request.get('/attendance/today', { params: { userId } }),

  getMyAttendance: (userId, startDate, endDate) => request.get('/attendance/my', {
    params: { userId, startDate, endDate }
  }),

  getAttendanceList: (params) => request.get('/attendance', { params }),

  getMonthlySummary: (userId, year, month) => request.get(`/attendance/summary/${userId}`, {
    params: { year, month }
  }),

  getMonthlySummaryList: (params) => request.get('/attendance/summary', { params })
}
