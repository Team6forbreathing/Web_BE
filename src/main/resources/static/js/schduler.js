const statuses = {
    counting: true,
    file: true
};

$(document).ready(getStatus())

function getStatus() {
    getFileStatus()
    getCountStatus()
}

function getCountStatus(){
    $.ajax({
        type: "GET",
        url: "/api/admin/scheduler/count",
        success : function(res){
            statuses.counting = res;
            document.getElementById(`counting-status`).textContent = `상태: ${statuses.counting ? 'ON' : 'OFF'}`;
        },
        error : function(error){
            alert("권한이 없습니다");
            window.location.href = '/admin/login';
        }
    })
}

function getFileStatus() {
    $.ajax({
        type: "GET",
        url: "/api/admin/scheduler/file",
        success : function(res){
            statuses.file = res;
            document.getElementById(`file-status`).textContent = `상태: ${statuses.file ? 'ON' : 'OFF'}`;
        },
        error : function(error){
            alert("권한이 없습니다");
            window.location.href = '/admin/login';
        }
    })
}

function toggleStatus(type) {
    if(type === 'counting') {
        changeCountStatus()
    }
    else {
        changeFileStatus()
    }
}

function changeCountStatus() {
    $.ajax({
        type: "POST",
        url: "/api/admin/scheduler/count",
        success : function(res){
            getStatus();
            if(statuses.counting) {
                alert("파일 개수 동기화 스케줄링을 시작했습니다.")
            }
            else {
                alert("파일 개수 동기화 스케줄링을 중단했습니다.")
            }
        },
        error : function(error){
            alert("권한이 없습니다");
            window.location.href = '/admin/login';
        }
    })
}

function changeFileStatus() {
    $.ajax({
        type: "POST",
        url: "/api/admin/scheduler/file",
        success : function(res){
            getStatus();
            if(statuses.file) {
                alert("파일 시스템 동기화 스케줄링을 시작했습니다.")
            }
            else {
                alert("파일 시스템 동기화 스케줄링을 중단했습니다.")
            }
        },
        error : function(error){
            alert("권한이 없습니다");
            window.location.href = '/admin/login';
        }
    })
}

function runScheduler(type) {
    if(type === 'counting') {
        launchCountScheduler()
    }
    else {
        launchFileScheduler()
    }
}

function launchCountScheduler() {
    $.ajax({
        type: "POST",
        url: "/api/admin/scheduler/count/launch",
        success : function(res){
            alert("파일 개수 동기화 동작을 실행했습니다.")
        },
        error : function(error){
            alert("권한이 없습니다");
            window.location.href = '/admin/login';
        }
    })
}

function launchFileScheduler() {
    $.ajax({
        type: "POST",
        url: "/api/admin/scheduler/file/launch",
        success : function(res){
            alert("파일 시스템 동기화 동작을 실행했습니다.")
        },
        error : function(error){
            alert("권한이 없습니다");
            window.location.href = '/admin/login';
        }
    })
}