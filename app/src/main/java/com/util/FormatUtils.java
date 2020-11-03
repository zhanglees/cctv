package com.util;

import com.rfid.config.CMD;
import com.rfid.config.ERROR;
import com.cctv.device.R;
import com.cctv.UHFApplication;

/**
 * @author naz
 * Email 961057759@qq.com
 * Date 2020/1/2
 */
public class FormatUtils {

    /**
     * 命令码和错误码解析
     *
     * @param cmd       命令码
     * @param errorCode 错误码
     * @return str
     */
    public static String format(byte cmd, byte errorCode) {
        if (errorCode == ERROR.SUCCESS) {
            return cmdFormat(cmd) + UHFApplication.getContext().getResources()
                    .getString(R.string.command_succeeded);
        } else if (errorCode == ERROR.FAIL) {
            return cmdFormat(cmd) + UHFApplication.getContext().getResources()
                    .getString(R.string.command_failed);
        }
        return cmdFormat(cmd) + UHFApplication.getContext().getResources()
                .getString(R.string.failure_reason) + errorFormat(errorCode);
    }

    /**
     * 命令码解析
     *
     * @param cmd 命令码
     * @return str
     */
    public static String cmdFormat(byte cmd) {
        String strCmd;
        switch (cmd) {
            case CMD.RESET:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.reset);
                break;
            case CMD.SET_UART_BAUDRATE:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.set_uart_baudrate);
                break;
            case CMD.GET_FIRMWARE_VERSION:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.get_firmware_version);
                break;
            case CMD.SET_READER_ADDRESS:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.set_reader_add);
                break;
            case CMD.SET_WORK_ANTENNA:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.set_work_ant);
                break;
            case CMD.GET_WORK_ANTENNA:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.get_work_ant);
                break;
            case CMD.SET_OUTPUT_POWER:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.set_output_power);
                break;
            case CMD.GET_OUTPUT_POWER:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.get_output_power);
                break;
            case CMD.SET_FREQUENCY_REGION:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.set_freq_reg);
                break;
            case CMD.GET_FREQUENCY_REGION:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.get_freq_reg);
                break;
            case CMD.SET_BEEPER_MODE:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.set_beeper_mode);
                break;
            case CMD.GET_READER_TEMPERATURE:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.get_rader_temp);
                break;
            case CMD.READ_GPIO_VALUE:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.read_gpio_value);
                break;
            case CMD.WRITE_GPIO_VALUE:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.write_gpio_value);
                break;
            case CMD.SET_ANT_CONNECTION_DETECTOR:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.set_ant_conn);
                break;
            case CMD.GET_ANT_CONNECTION_DETECTOR:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.get_ant_conn);
                break;
            case CMD.SET_TEMPORARY_OUTPUT_POWER:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.set_temp_output_power);
                break;
            case CMD.SET_READER_IDENTIFIER:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.set_reader_identi);
                break;
            case CMD.GET_READER_IDENTIFIER:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.get_reader_identi);
                break;
            case CMD.SET_RF_LINK_PROFILE:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.set_rf_link_pro);
                break;
            case CMD.GET_RF_LINK_PROFILE:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.get_rf_link_pro);
                break;
            case CMD.GET_RF_PORT_RETURN_LOSS:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.get_rf_port);
                break;
            case CMD.INVENTORY:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.inventory);
                break;
            case CMD.READ_TAG:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.read_tag_c);
                break;
            case CMD.WRITE_TAG:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.write_tag_c);
                break;
            case CMD.LOCK_TAG:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.lock_tag_c);
                break;
            case CMD.KILL_TAG:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.kill_tag_c);
                break;
            case CMD.SET_ACCESS_EPC_MATCH:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.set_access_epc_match);
                break;
            case CMD.GET_ACCESS_EPC_MATCH:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.get_access_epc_match);
                break;
            case CMD.REAL_TIME_INVENTORY:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.real_time_inventory);
                break;
            case CMD.FAST_SWITCH_ANT_INVENTORY:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.fast_switch_ant_inv);
                break;
            case CMD.CUSTOMIZED_SESSION_TARGET_INVENTORY:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.customized_session);
                break;
            case CMD.SET_IMPINJ_FAST_TID:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.set_impinj);
                break;
            case CMD.SET_AND_SAVE_IMPINJ_FAST_TID:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.set_and_save_impinj);
                break;
            case CMD.GET_IMPINJ_FAST_TID:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.get_imping);
                break;
            case CMD.ISO18000_6B_INVENTORY:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.iso_B_inv);
                break;
            case CMD.ISO18000_6B_READ_TAG:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.iso_B_read);
                break;
            case CMD.ISO18000_6B_WRITE_TAG:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.iso_b_write_tag);
                break;
            case CMD.ISO18000_6B_LOCK_TAG:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.iso_b_lock_tag);
                break;
            case CMD.ISO18000_6B_QUERY_LOCK_TAG:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.iso_b_query_lock_tag);
                break;
            case CMD.GET_INVENTORY_BUFFER:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.get_inventory_buff);
                break;
            case CMD.GET_AND_RESET_INVENTORY_BUFFER:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.get_ant_reset_inv);
                break;
            case CMD.GET_INVENTORY_BUFFER_TAG_COUNT:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.get_inventory_buffer_tag_count);
                break;
            case CMD.RESET_INVENTORY_BUFFER:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.reset_inventory_buff);
                break;
            case CMD.QUERY_READER_STATUS:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.query_reader_status);
                break;
            case CMD.SET_READER_STATUS:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.set_reader_status);
                break;
            default:
                strCmd = UHFApplication.getContext().getResources().getString(R.string.unknown_operate);
                break;
        }
        return strCmd;
    }

    /**
     * 错误码解析
     *
     * @param errorCode 错误码
     * @return str
     */
    private static String errorFormat(byte errorCode) {
        String strErrorCode;
        switch (errorCode) {
//            case ERROR.SUCCESS:
//                strErrorCode = UHFApplication.getContext().getResources()
//                        .getString(R.string.command_succeeded);
//                break;
//            case ERROR.FAIL:
//                strErrorCode = UHFApplication.getContext().getResources()
//                        .getString(R.string.command_failed);
//                break;
            case ERROR.MCU_RESET_ERROR:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.cup_seset_error);
                break;
            case ERROR.CW_ON_ERROR:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.cw_on_error);
                break;
            case ERROR.ANTENNA_MISSING_ERROR:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.antenna_miss_error);
                break;
            case ERROR.WRITE_FLASH_ERROR:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.write_flash_error);
                break;
            case ERROR.READ_FLASH_ERROR:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.Read_flash_error);
                break;
            case ERROR.SET_OUTPUT_POWER_ERROR:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.set_output_power_error);
                break;
            case ERROR.TAG_INVENTORY_ERROR:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.tag_inventory_error);
                break;
            case ERROR.TAG_READ_ERROR:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.tag_read_error);
                break;
            case ERROR.TAG_WRITE_ERROR:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.tag_write_error);
                break;
            case ERROR.TAG_LOCK_ERROR:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.tag_lock_error);
                break;
            case ERROR.TAG_KILL_ERROR:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.tag_kill_error);
                break;
            case ERROR.NO_TAG_ERROR:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.no_tag_error);
                break;
            case ERROR.INVENTORY_OK_BUT_ACCESS_FAIL:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.inventory_ok_but_access_fail);
                break;
            case ERROR.BUFFER_IS_EMPTY_ERROR:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.buffer_is_empty_error);
                break;
            case ERROR.ACCESS_OR_PASSWORD_ERROR:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.access_ro_password_error);
                break;
            case ERROR.PARAMETER_INVALID:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.parameter_invalid);
                break;
            case ERROR.PARAMETER_INVALID_WORDCNT_TOO_LONG:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.parameter_invalid_wordcnt_too_long);
                break;
            case ERROR.PARAMETER_INVALID_MEMBANK_OUT_OF_RANGE:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.parameter_invaild_membank_out_of_range);
                break;
            case ERROR.PARAMETER_INVALID_LOCK_REGION_OUT_OF_RANGE:
                strErrorCode = UHFApplication
                        .getContext()
                        .getResources()
                        .getString(
                                R.string.parameter_invaild_lock_region_out_of_range);
                break;
            case ERROR.PARAMETER_INVALID_LOCK_ACTION_OUT_OF_RANGE:
                strErrorCode = UHFApplication
                        .getContext()
                        .getResources()
                        .getString(
                                R.string.parameter_invaild_lock_action_out_of_range);
                break;
            case ERROR.PARAMETER_READER_ADDRESS_INVALID:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.parameter_reader_address_invaild);
                break;
            case ERROR.PARAMETER_INVALID_ANTENNA_ID_OUT_OF_RANGE:
                strErrorCode = UHFApplication
                        .getContext()
                        .getResources()
                        .getString(
                                R.string.parameter_invaild_antenna_id_out_of_range);
                break;
            case ERROR.PARAMETER_INVALID_OUTPUT_POWER_OUT_OF_RANGE:
                strErrorCode = UHFApplication
                        .getContext()
                        .getResources()
                        .getString(
                                R.string.parameter_invaild_output_power_out_of_range);
                break;
            case ERROR.PARAMETER_INVALID_FREQUENCY_REGION_OUT_OF_RANGE:
                strErrorCode = UHFApplication
                        .getContext()
                        .getResources()
                        .getString(
                                R.string.parameter_invaild_frequency_region_out_of_range);
                break;
            case ERROR.PARAMETER_INVALID_BAUDRATE_OUT_OF_RANGE:
                strErrorCode = UHFApplication
                        .getContext()
                        .getResources()
                        .getString(R.string.parameter_invaild_baudrate_out_of_range);
                break;
            case ERROR.PARAMETER_BEEPER_MODE_OUT_OF_RANGE:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.parameter_beeper_mode_out_of_range);
                break;
            case ERROR.PARAMETER_EPC_MATCH_LEN_TOO_LONG:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.parameter_epc_match_len_too_long);
                break;
            case ERROR.PARAMETER_EPC_MATCH_LEN_ERROR:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.parameter_epc_match_len_error);
                break;
            case ERROR.PARAMETER_INVALID_EPC_MATCH_MODE:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.parameter_invaild_epc_match_mode);
                break;
            case ERROR.PARAMETER_INVALID_FREQUENCY_RANGE:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.parameter_invaild_frequency_range);
                break;
            case ERROR.FAIL_TO_GET_RN16_FROM_TAG:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.fail_to_get_rn16_from_tag);
                break;
            case ERROR.PARAMETER_INVALID_DRM_MODE:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.parameter_invaild_drm_mode);
                break;
            case ERROR.PLL_LOCK_FAIL:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.pll_lock_fail);
                break;
            case ERROR.RF_CHIP_FAIL_TO_RESPONSE:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.rf_chip_fail_to_response);
                break;
            case ERROR.FAIL_TO_ACHIEVE_DESIRED_OUTPUT_POWER:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.fail_to_chieve_desired_output_power);
                break;
            case ERROR.COPYRIGHT_AUTHENTICATION_FAIL:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.copyright_authentication_fail);
                break;
            case ERROR.SPECTRUM_REGULATION_ERROR:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.spectrum_regulation_error);
                break;
            case ERROR.OUTPUT_POWER_TOO_LOW:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.output_power_too_low);
                break;
            default:
                strErrorCode = UHFApplication.getContext().getResources()
                        .getString(R.string.unknown_error);
                break;
        }
        return strErrorCode;
    }
}

