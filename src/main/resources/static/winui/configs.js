YL.static = {
  softwareName: 'IJAdmin', //软件名
  version: "1.0.0",
  iconBtnStart: 'html5', //主图标
  author: 'Levin',//作者
  contactInformation: 'levin',//联系方式
  officialWebsite: 'levin',//软件官网
  welcome: '',//加载完毕控制台提示信息
  copyrightDetail: 'All Rightes Reserved !',//版权信息信息
  otherStatements: '',//其他信息（可留空）

  /**lang:'zh-cn', 语言————————————————————————————————————————————————————————————————————————————————————————————*/
  lang: 'zh-cn', //语言
  localStorageName: "ylui-storage", //ls存储名
  lockedApps: ['yl-system', 'yl-color-picker', 'yl-browser', 'yl-server'],
  trustedApps: ['yl-server'],
  debug: false,//启用更多调试信息
  beforeOnloadEnable: true,//启用意外重载询问（打包app时请关闭防止出错）
  WarningPerformanceInIE: true,//在IE下提示体验不佳信息
  languages: {}, //推荐留空，自动从文件加载
  changeable: true,//存档数据是否可被普通用户修改
  dataCenter: true,//是否展示数据管理中心


};