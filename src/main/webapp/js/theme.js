var saveSelectColor = {
    'Name': 'SelcetColor',
    'Color': 'theme-white'
}



// 判断用户是否已有自己选择的模板风格



// 本地缓存
function storageSave(objectData) {
    localStorage.setItem(objectData.Name, JSON.stringify(objectData));
}

function storageLoad(objectName) {
    if (localStorage.getItem(objectName)) {
        return JSON.parse(localStorage.getItem(objectName))
    } else {
        return false
    }
}