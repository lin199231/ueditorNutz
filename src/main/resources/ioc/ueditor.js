var ioc = {
    ueditorProperties : {
        type : "com.baidu.ueditor.config.UeditorProperties",
        args : [
            {java:"$conf.get('ueditor.endpoint')"},
            {java:"$conf.get('ueditor.accessKeyId')"},
            {java:"$conf.get('ueditor.accessKeySecret')"}
        ],
        fields : {
            bucket : {java:"$conf.get('ueditor.bucket')"},
            baseUrl : {java:"$conf.get('ueditor.baseUrl')"},
            uploadDirPrefix : {java:"$conf.get('ueditor.uploadDirPrefix')"}
        }
    },
    configManager : {
        type : "com.baidu.ueditor.ConfigManager",
        args : [
            {java:"$conf.get('ueditor.config')"}
        ]
    },
    actionEnter : {
        type : "com.baidu.ueditor.ActionEnter",
        args : [
            {refer:"configManager"}
        ],
        fields : {
            fileManager : {refer:"fileManager"},
            storageManager : {refer:"storageManager"}
        }
    },
    fileManager : {
        type : "com.baidu.ueditor.hunter.FileManager",
        fields : {
            ueditorProperties : {refer:"ueditorProperties"}
        }
    },
    storageManager : {
        type : "com.baidu.ueditor.upload.StorageManager",
        fields : {
            ueditorProperties : {refer:"ueditorProperties"}
        }
    }
};