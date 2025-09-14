import request from '../utils/request';

// 获取案件列表
export const getCaseList = (params) => {
  return request({
    url: '/cases',
    method: 'get',
    params,
  });
};

// 获取案件详情
export const getCaseDetail = (caseId) => {
  return request({
    url: `/cases/${caseId}`,
    method: 'get',
  });
};

// 上传文件
export const uploadFile = (caseId, formData) => {
  return request({
    url: `/cases/${caseId}/upload`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

// 获取文件内容
export const getFileContent = (caseId, fileId) => {
  return request({
    url: `/cases/${caseId}/files/${fileId}`,
    method: 'get',
  });
};