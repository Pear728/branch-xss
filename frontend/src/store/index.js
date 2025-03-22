import { createStore } from 'vuex'
import api from '../api'

export default createStore({
  state: {
    attackLogs: [],
    defenseConfig: {
      enableInputValidation: true,
      enableOutputEncoding: true,
      enableCsp: true,
      cspPolicy: "default-src 'self'; script-src 'self'"
    },
    testResults: null,
    isLoading: false
  },
  getters: {
    attackLogsCount(state) {
      return state.attackLogs.length
    },
    getDefenseConfig(state) {
      return state.defenseConfig
    }
  },
  mutations: {
    SET_ATTACK_LOGS(state, logs) {
      state.attackLogs = logs
    },
    ADD_ATTACK_LOG(state, log) {
      state.attackLogs.unshift(log)
    },
    SET_DEFENSE_CONFIG(state, config) {
      state.defenseConfig = config
    },
    SET_TEST_RESULTS(state, results) {
      state.testResults = results
    },
    SET_LOADING(state, status) {
      state.isLoading = status
    }
  },
  actions: {
    async fetchAttackLogs({ commit }) {
      commit('SET_LOADING', true)
      try {
        const response = await api.getAttackLogs()
        commit('SET_ATTACK_LOGS', response.data)
      } catch (error) {
        console.error('Failed to fetch attack logs:', error)
      } finally {
        commit('SET_LOADING', false)
      }
    },
    async saveDefenseConfig({ commit }, config) {
      commit('SET_LOADING', true)
      try {
        const response = await api.updateDefenseConfig(config)
        commit('SET_DEFENSE_CONFIG', response.data)
        return true
      } catch (error) {
        console.error('Failed to save defense config:', error)
        return false
      } finally {
        commit('SET_LOADING', false)
      }
    },
    async runXssTest({ commit, dispatch }, payload) {
      commit('SET_LOADING', true)
      try {
        const response = await api.runXssTest(payload)
        commit('SET_TEST_RESULTS', response.data)
        
        // 测试后自动刷新攻击日志和统计
        await dispatch('fetchAttackLogs')
        
        return response.data
      } catch (error) {
        console.error('Failed to run XSS test:', error)
        return null
      } finally {
        commit('SET_LOADING', false)
      }
    },
    async clearAttackLogs({ commit }) {
      commit('SET_LOADING', true)
      try {
        await api.clearAttackLogs()
        commit('SET_ATTACK_LOGS', [])
        return true
      } catch (error) {
        console.error('Failed to clear attack logs:', error)
        return false
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async fetchLatestAttackLog({ commit }, count = 1) {
      commit('SET_LOADING', true)
      try {
        const response = await api.getLatestAttackLog(count)
        if (response.data && response.data.length > 0) {
          return response.data[0]
        }
        return null
      } catch (error) {
        console.error('Failed to fetch latest attack log:', error)
        return null
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async fetchAttackLogById({ commit }, id) {
      commit('SET_LOADING', true)
      try {
        const response = await api.getAttackLogById(id)
        return response.data
      } catch (error) {
        console.error(`Failed to fetch attack log with ID ${id}:`, error)
        return null
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async getDatabaseInfo({ commit }) {
      commit('SET_LOADING', true)
      try {
        const response = await api.getDatabaseInfo()
        return response.data
      } catch (error) {
        console.error('Failed to get database info:', error)
        return null
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async getSystemInfo({ commit }) {
      commit('SET_LOADING', true)
      try {
        const response = await api.getSystemInfo()
        return response.data
      } catch (error) {
        console.error('Failed to get system info:', error)
        return null
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async fixDatabase({ commit }) {
      commit('SET_LOADING', true)
      try {
        const response = await api.fixDatabase()
        return response.data
      } catch (error) {
        console.error('Failed to fix database:', error)
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    }
  }
}) 