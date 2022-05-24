set -x
export JOB_NAME='Jenkins_Job_Config_Backup' && export WORKSPACE='/home/abc/.jenkins/workspace/Jenkins_Job_Config_Backup' && export PYTHONPATH='/home/abc/.jenkins/workspace/Jenkins_Job_Config_Backup'
python3 /home/abc/.jenkins/workspace/Jenkins_Job_Config_Backup/python_tests/backup_jenkins_configs/JenkinsBackup.py 

echo 's3 file sync'
aws s3 sync --exclude="*" --include="*.zip" . s3://nykaa-non-prod-returns/jenkins_config_backup
