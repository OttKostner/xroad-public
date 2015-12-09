#
# The MIT License
# Copyright (c) 2015 Estonian Information System Authority (RIA), Population Register Centre (VRK)
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
#

java_import Java::ee.ria.xroad.common.ConfClientStatus
java_import Java::ee.ria.xroad.common.ConfClientErrorCodes
java_import Java::ee.ria.xroad.common.util.JsonUtils

class DiagnosticsController < ApplicationController

  def index
    logger.info("Diagnostics index")
    authorize!(:diagnostics)
    response = query_confclient_status
    returnCode = response.getReturnCode()
    @statusMessage = getStatusMessage(returnCode)
    if returnCode == ConfClientErrorCodes::RETURN_SUCCESS
      @statusClass = "diagnostics_status_ok"
    elsif returnCode == ConfClientErrorCodes::ERROR_CODE_UNINITIALIZED
      @statusClass = "diagnostics_status_waiting"
    else
      @statusClass = "diagnostics_status_fail"
    end
    @prevUpdate = response.getFormattedPrevUpdate()
    @nextUpdate = response.getFormattedNextUpdate()
  end

  def query_confclient_status
    logger.info("Query configuration client status")

    port = SystemProperties::getConfigurationClientPort() + 1
    uri = URI("http://localhost:#{port}/status")

    response = nil

    begin
      response = Net::HTTP.get_response(uri)
      logger.info("Response code: " + response.code + " message: " + response.message + " body: " + response.body)
    rescue
      log_stacktrace($!)
      raise t('diagnostics.confclient_status_failed', :response => $!.message)
    end

    if response.code == '500'
      logger.error(response.body)
      raise t('diagnostics.confclient_status_failed', :response => response.body)
    end

    gson = JsonUtils::getSerializer()
    return gson.fromJson(response.body, ConfClientStatus.java_class)
  end

  private

  def getStatusMessage (returnCode)
    case
      when returnCode == ConfClientErrorCodes::RETURN_SUCCESS
        t('diagnostics.return_success')
      when returnCode == ConfClientErrorCodes::ERROR_CODE_INTERNAL
        t('diagnostics.error_code_internal')
      when returnCode == ConfClientErrorCodes::ERROR_CODE_INVALID_SIGNATURE_VALUE
        t('diagnostics.error_code_invalid_signature_value')
      when returnCode == ConfClientErrorCodes::ERROR_CODE_EXPIRED_CONF
        t('diagnostics.error_code_expired_conf')
      when returnCode == ConfClientErrorCodes::ERROR_CODE_CANNOT_DOWNLOAD_CONF
        t('diagnostics.error_code_cannot_download_conf')
      when returnCode == ConfClientErrorCodes::ERROR_CODE_MISSING_PRIVATE_PARAMS
        t('diagnostics.error_code_missing_private_params')
      when returnCode == ConfClientErrorCodes::ERROR_CODE_UNINITIALIZED
        t('diagnostics.error_code_uninitialized')
      else
        t('diagnostics.error_code_unknown')
    end
  end

end
