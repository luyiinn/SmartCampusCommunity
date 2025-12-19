import { describe, it, expect, vi, beforeEach } from 'vitest';
import { mount } from '@vue/test-utils';
import UserProfile from '../UserProfile.vue';
import { useUserStore } from '../../stores/userStore';
import axios from 'axios';
import { ElMessage } from 'element-plus';

// Mock dependencies
vi.mock('../../stores/userStore', () => ({
  useUserStore: vi.fn(() => ({
    isLoggedIn: true,
    token: 'test-token',
    user: {
      id: 1,
      name: 'Test User',
      avatar: 'test-avatar.png'
    },
    requireLogin: vi.fn()
  }))
}));

vi.mock('axios', () => ({
  default: {
    get: vi.fn(),
    put: vi.fn()
  }
}));

vi.mock('element-plus', () => ({
  ElMessage: {
    error: vi.fn(),
    success: vi.fn(),
    warning: vi.fn()
  }
}));

describe('UserProfile.vue - Like Functionality', () => {
  const mockAxios = axios as vi.Mocked<typeof axios>;
  const mockUserStore = useUserStore() as vi.Mocked<ReturnType<typeof useUserStore>>;
  const mockElMessage = ElMessage as vi.Mocked<typeof ElMessage>;

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should call correct API when liking a post', async () => {
    // Mock API response
    mockAxios.get.mockResolvedValueOnce({
      data: {
        code: 1,
        data: {
          userId: 1,
          userName: 'Test User',
          avatar: 'test-avatar.png',
          nickname: 'Test Nickname',
          gender: 1,
          bio: 'Test Bio',
          followerCount: 10,
          followingCount: 5,
          postCount: 2
        }
      }
    });

    mockAxios.get.mockResolvedValueOnce({
      data: {
        code: 1,
        data: {
          total: 1,
          list: [{
            id: 1,
            userId: 1,
            userName: 'Test User',
            avatar: 'test-avatar.png',
            title: 'Test Post',
            contentSummary: 'Test Content',
            isAnonymous: 0,
            status: 1,
            viewCount: 10,
            likeCount: 5,
            commentCount: 2,
            createdAt: '2023-01-01T00:00:00Z',
            tags: ['test'],
            isLike: 0,
            images: ['test-image.png']
          }]
        }
      }
    });

    mockAxios.put.mockResolvedValueOnce({
      data: {
        code: 1
      }
    });

    // Mount the component
    const wrapper = mount(UserProfile, {
      global: {
        stubs: ['router-link', 'ElMessage', 'ElMessageBox', 'PostList', 'UserProfileHeader']
      }
    });

    // Wait for component to load
    await wrapper.vm.$nextTick();

    // Simulate like status update event from PostList
    await wrapper.vm.handleLikeStatusUpdate(1, 1);

    // Check if API was called with correct URL and method
    expect(mockAxios.put).toHaveBeenCalledWith(
      '/post/like/1',
      {},
      expect.objectContaining({
        headers: expect.objectContaining({
          token: ' test-token'
        })
      })
    );

    // Check if ElMessage was not called (success case)
    expect(mockElMessage.error).not.toHaveBeenCalled();
  });

  it('should handle API error when liking a post', async () => {
    // Mock API response
    mockAxios.get.mockResolvedValueOnce({
      data: {
        code: 1,
        data: {
          userId: 1,
          userName: 'Test User',
          avatar: 'test-avatar.png',
          nickname: 'Test Nickname',
          gender: 1,
          bio: 'Test Bio',
          followerCount: 10,
          followingCount: 5,
          postCount: 2
        }
      }
    });

    mockAxios.get.mockResolvedValueOnce({
      data: {
        code: 1,
        data: {
          total: 1,
          list: [{
            id: 1,
            userId: 1,
            userName: 'Test User',
            avatar: 'test-avatar.png',
            title: 'Test Post',
            contentSummary: 'Test Content',
            isAnonymous: 0,
            status: 1,
            viewCount: 10,
            likeCount: 5,
            commentCount: 2,
            createdAt: '2023-01-01T00:00:00Z',
            tags: ['test'],
            isLike: 0,
            images: ['test-image.png']
          }]
        }
      }
    });

    mockAxios.put.mockRejectedValueOnce(new Error('Network Error'));

    // Mount the component
    const wrapper = mount(UserProfile, {
      global: {
        stubs: ['router-link', 'ElMessage', 'ElMessageBox', 'PostList', 'UserProfileHeader']
      }
    });

    // Wait for component to load
    await wrapper.vm.$nextTick();

    // Simulate like status update event from PostList
    await wrapper.vm.handleLikeStatusUpdate(1, 1);

    // Check if ElMessage was called with error message
    expect(mockElMessage.error).toHaveBeenCalledWith(expect.any(String));
  });
});
